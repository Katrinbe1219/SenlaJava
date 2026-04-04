package com.example.application.services;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.hibernate.OrderHibImplementation;
import com.example.application.hibernate.RequestHibImpl;
import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.types.BookStatus;
import com.example.application.model.types.OrderSorting;
import com.example.application.model.types.OrderStatus;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookShopFacadeTest {

    @Mock
    RequestHibImpl requestHibImpl;

    @Mock
    OrderHibImplementation orderHibImpl;

    @Mock
    Logger logger;

    @InjectMocks
    BookShopFacade bookShopFacade;

    private List<Order> getOrdersList(){
        List<Order> orders = new ArrayList<>();
        Book book = new Book();
        book.setPrice(1000);
        Order order = new Order();
        order.addBook(book);
        orders.add(order);
        return orders;
    }

    private Order getOrder(){
        Book book = new Book();
        book.setPrice(1000);
        book.setStatus(BookStatus.IN_STOCK);
        Order order = new Order();
        order.addBook(book);
        order.setStatus(OrderStatus.DONE);

        return order;
    }

    // ── createOrder ────────────────────────────────────────────
    @Test
    @DisplayName("createOrderIfOrderWasCreated")
    public void createOrderIfOrderWasCreated() {
        Order order = getOrder();
        doNothing().when(orderHibImpl).save(any(Order.class), eq(logger));
        when(requestHibImpl.insertMany(anyList(), any(Order.class), eq(logger))).thenReturn(null);
        bookShopFacade.createOrder(order, logger);
        verify(orderHibImpl).save(any(Order.class), eq(logger));
        verify(logger, never()).error(any(String.class));
    }

    @Test
    @DisplayName("createOrderIfSavingWasFailed")
    public void createOrderIfSavingWasFailed() {
        try (MockedStatic<TransactionAspectSupport> mockedStatic = mockStatic(TransactionAspectSupport.class)){
            TransactionStatus mockStatus = Mockito.mock(TransactionStatus.class);
            mockedStatic.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);

            Order order = getOrder();
            doThrow(new CanNotMakeExecution("pr")).when(orderHibImpl).save(any(Order.class), eq(logger));
            
            Assertions.assertThrows(CanNotMakeExecution.class, () ->bookShopFacade.createOrder(order, logger));
            verify(orderHibImpl).save(any(Order.class), eq(logger));
            verify(logger).error(contains("pr"));
        }

    }

    @Test
    @DisplayName("removeOrderIfOrderWasRemoved")
    public void removeOrderIfOrderWasRemoved() {
        Order order = new Order();
        doNothing().when(orderHibImpl).update(eq(order), eq(logger));
        Assertions.assertTrue(bookShopFacade.removeOrder(order, logger));
    }



    @Test
    @DisplayName("removeOrderIfUpdatingWasFailed")
    public void removeOrderIfUpdatingWasFailed() {
        Order order = new Order();
        // если написать Exception - checked exception, а метод изначально не помечен как бросающий
        // будет проблема
        // бросаем то, что прописанно
        doThrow(new CanNotMakeExecution("pr")).when(orderHibImpl).update(eq(order), eq(logger));

        Assertions.assertFalse(bookShopFacade.removeOrder(order, logger));
        verify(logger).error("Проблема при получении заказов SQl type: pr");
    }

    // ── updateOrders ───────────────────────────────────────────
    @Test
    @DisplayName("updateOrdersIfOrderWasUpdated")
    public void updateOrdersIfOrderWasUpdated() {
        List<Order> order = new ArrayList<>();
        doNothing().when(orderHibImpl).update(eq(order), eq(logger));
        bookShopFacade.updateOrders(order, logger);
        verify(orderHibImpl).update(eq(order), eq(logger));
        verify(logger, never()).error(anyString());
    }

    @Test
    @DisplayName("updateOrdersIfUpdatingWasFailed")
    public void updateOrdersIfUpdatingWasFailed() {
        List<Order> order = new ArrayList<>();
        doThrow(CanNotMakeExecution.class).when(orderHibImpl).update(eq(order), eq(logger));
        bookShopFacade.updateOrders(order, logger);

        verify(orderHibImpl).update(eq(order), eq(logger));
        verify(logger).error(anyString());
    }


    // ── getSortedOrders ────────────────────────────────────────
    @Test
    @DisplayName("getSortedOrdersIfSoringWasFailed")
    public void getSortedOrdersIfSoringWasFailed() {
        when(orderHibImpl.getOrdersSorted(OrderStatus.DONE, logger)).thenThrow(new CanNotMakeExecution("pr"));
        Assertions.assertNull(bookShopFacade.getSortedOrders(OrderSorting.DONE, logger) );
        verify(logger).error("Проблема при получении заказов SQl type: pr");
    }

    @Test
    @DisplayName("getSortedOrdersIfOrdersWereFetched")
    public void getSortedOrdersIfOrdersWereFetched() {
        List<Order> orders = getOrdersList();
        when(orderHibImpl.getOrdersSorted(OrderStatus.DONE, logger)).thenReturn(orders);
        Assertions.assertEquals(orders,bookShopFacade.getSortedOrders(OrderSorting.DONE, logger) );
    }

    // ── getDoneOrdersInDiapazon ────────────────────────────────
    @Test
    @DisplayName("getDoneOrdersInDiapazonIfOrdersWereFetched")
    public void getDoneOrdersInDiapazonIfOrdersWereFetched() {
        List<Order> orders = getOrdersList();
        when(orderHibImpl.getSortedDoneOrders(any(LocalDate.class), any(LocalDate.class), eq(logger))).thenReturn(orders);
        Assertions.assertEquals(orders, bookShopFacade.getDoneOrdersInDiapazon( LocalDate.now(), LocalDate.now(),OrderSorting.PRICE_UP, logger));
    }

    @Test
    @DisplayName("getDoneOrdersInDiapazonIfSortingWasFailed")
    public void getDoneOrdersInDiapazonIfSortingWasFailed() {
        when(orderHibImpl.getSortedDoneOrders(any(LocalDate.class), any(LocalDate.class), eq(logger))).thenThrow(new CanNotMakeExecution("pr"));
        Assertions.assertNull( bookShopFacade.getDoneOrdersInDiapazon( LocalDate.now(), LocalDate.now(),OrderSorting.PRICE_UP, logger));
        verify(logger).error(contains("pr"));
    }

    // ── getOrdersAmountInDiapazon ──────────────────────────────
    @Test
    @DisplayName("getOrdersAmountInDiapazonIfOrdersWereFetched")
    public void getOrdersAmountInDiapazonIfOrdersWereFetched() {
        List<Order> orders = getOrdersList();
        when(orderHibImpl.getOrdersInDiapazon(any(LocalDate.class), any(LocalDate.class), eq(logger)))
                .thenReturn(orders);
        Assertions.assertEquals(1, bookShopFacade.getOrdersAmountInDiapazon(LocalDate.now(), LocalDate.now(), logger));
    }

    @Test
    @DisplayName("getOrdersAmountInDiapazonIfFetchingWasFailed")
    public void getOrdersAmountInDiapazonIfFetchingWasFailed() {
        when(orderHibImpl.getOrdersInDiapazon(any(LocalDate.class), any(LocalDate.class), eq(logger)))
                .thenThrow(CanNotMakeExecution.class);
        bookShopFacade.getOrdersAmountInDiapazon(LocalDate.now(), LocalDate.now(), logger);
        verify(logger).error(anyString());
    }

    // ── getIncomeInDiapazon ────────────────────────────────────
    @Test
    @DisplayName("getIncomeInDiapazonIfOrdersWereFetched")
    public void getIncomeInDiapazonIfOrdersWereFetched() {
        when(orderHibImpl.getOrdersInDiapazon(any(LocalDate.class), any(LocalDate.class), eq(logger)))
                .thenReturn(getOrdersList());
        Assertions.assertEquals(1000, bookShopFacade.getIncomeInDiapazon(LocalDate.now(), LocalDate.now(), logger));
    }

    @Test
    @DisplayName("getIncomeInDiapazonIfFetchingWasFailed")
    public void getIncomeInDiapazonIfFetchingWasFailed() {
        when(orderHibImpl.getOrdersInDiapazon(any(LocalDate.class), any(LocalDate.class), eq(logger)))
                .thenThrow(new CanNotMakeExecution("pr"));
        Assertions.assertEquals(0, bookShopFacade.getIncomeInDiapazon(LocalDate.now(), LocalDate.now(), logger));
        verify(logger).error(contains("pr"));
    }

    // ── getOrderById ───────────────────────────────────────────
    @Test
    @DisplayName("getOrderByIdIfOrderWasFetched")
    public void getOrderByIdIfOrderWasFetched() {
        when(orderHibImpl.getById(anyInt(), eq(logger)))
                .thenReturn(null);
        Assertions.assertNull(bookShopFacade.getOrderById(1, logger));
        verify(logger, never()).error(anyString());
    }

    @Test
    @DisplayName("getOrderByIdIfFetchingWasFailed")
    public void getOrderByIdIfFetchingWasFailed() {
        when(orderHibImpl.getById(anyInt(), eq(logger)))
                .thenThrow(new CanNotMakeExecution("pr"));
        Assertions.assertNull(bookShopFacade.getOrderById(1, logger));
        verify(logger).error(contains("pr"));
    }
}
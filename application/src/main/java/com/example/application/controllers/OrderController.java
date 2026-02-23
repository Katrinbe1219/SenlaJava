package com.example.application.controllers;

import com.example.application.dto.*;
import com.example.application.model.Author;
import com.example.application.model.Book;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.model.types.OrderSorting;
import com.example.application.model.types.OrderStatus;
import com.example.application.services.BookService;
import com.example.application.services.BookShopFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private BookShopFacade bookshop;
    private BookService bookService;
    private static final Logger logger = LogManager.getLogger(OrderController.class.getName());

    public OrderController(BookShopFacade bookshop, BookService bookService) {
        this.bookshop = bookshop;
        this.bookService = bookService;
    }
    // types for sorting
    //"1. Цена (по возрастанию)\n2. Цена (по убыванию)" +
    //                "3. Дата (по возрастанию)\n4. Дата (по убыванию)" +
    //                "5. Завершенные\n6. Незавершенные\n7. Отмененные";


    private OrderSorting getOrderSorting(String type){
        return switch (type){
            case "1" -> OrderSorting.PRICE_UP;
            case "2" -> OrderSorting.PRICE_DOWN;
            case "3" -> OrderSorting.DATE_UP;
            case "4" -> OrderSorting.DATE_DOWN;
            case "5" -> OrderSorting.DONE;
            case "6" -> OrderSorting.NEW;
            case "7" -> OrderSorting.CANCELLED;
            default -> OrderSorting.DATE_UP;
        };
    }

    @PostMapping(value = "/create", produces = "text/plain")
    public StrinResponse createOrder(@RequestBody OrderCreateDto order) {
        Order orderObject = new Order();
        orderObject.setCustomer(toCustomer(order.getCustomer()));

        List<Book> books = bookService.getBooksByTitles(order.getBooks(), logger);
        for (Book book : books) {
            orderObject.addBook(book);
        }

        ArrayList<Integer> done = bookshop.createOrder(orderObject, logger);
        if (done == null) {
            bookService.setLastPurchase(orderObject.getBooks(), logger);
            return new StrinResponse("Order created successfully");

        }else {
            return new StrinResponse("Order was created with requests " + done.toString());
        }

    }


    @GetMapping
    List<OrderDTO> getAllOrders(@RequestParam("type") String type){
        OrderSorting sorting = getOrderSorting(type);
        List<Order> orders = bookshop.getSortedOrders(sorting, logger);
        return orders.stream().map(this::toOrderDTO).toList();
    }

    @GetMapping("/{orderId}")
    OrderDTO getOrder(@PathVariable("orderId") int orderId){
        try {
            return toOrderDTO(bookshop.getOrderById(orderId, logger));

        }catch (NumberFormatException e){
            return null;
        }
    }

    @DeleteMapping("/delete/{orderId}")
    public StrinResponse deleteOrder(@PathVariable("orderId") int orderId) throws Exception{
        Order order = bookshop.getOrderById(orderId, logger);
        if (order == null) throw new Exception("Такого нет заказа");


        Boolean result = bookshop.removeOrder(order, logger);
        if (result){
            bookService.cancellOrderRequests(order, logger);
            logger.info("Обработка команды удаления в отсеке заказов завершена");
            return new StrinResponse("Удалено");
        }
        throw new Exception("Такого нет заказа");
    }

    @GetMapping("/diapazon")
    List<OrderDTO> displayOrdersInDiapazon(@RequestParam("firstDate") String firstDate,
                                        @RequestParam("secondDate") String secondDate,
                                        @RequestParam("type") String type){
        // yyyy-MM-dd
        try {
            LocalDate first = LocalDate.parse(firstDate);
            LocalDate second = LocalDate.parse(secondDate);
            OrderSorting sorting = getOrderSorting(type);
            List<Order> orders = bookshop.getDoneOrdersInDiapazon(first,second, sorting, logger);

            return orders.stream().map(this::toOrderDTO).toList();

        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            return null;
        }


    }




    @GetMapping(value = "/amount", produces= MediaType.APPLICATION_JSON_VALUE)
    StrinResponse displayOrderAmountInDiapazon(@RequestParam("firstDate") String firstDate,
                                     @RequestParam("secondDate") String secondDate){
        try {
            LocalDate first = LocalDate.parse(firstDate);
            LocalDate second = LocalDate.parse(secondDate);
            Integer orders = bookshop.getOrdersAmountInDiapazon(first,second, logger);

            return new StrinResponse("Количество заказов: " + String.valueOf(orders));
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            return new StrinResponse("Нет заказов");
        }

    }

    @GetMapping(value = "/income", produces= MediaType.APPLICATION_JSON_VALUE)
    StrinResponse displayIncomeInDiapazon(
            @RequestParam("firstDate") String firstDate,
            @RequestParam("secondDate") String secondDate){
        try {
            LocalDate first = LocalDate.parse(firstDate);
            LocalDate second = LocalDate.parse(secondDate);
            double orders = bookshop.getIncomeInDiapazon(first, second, logger);


            return new StrinResponse("Доход: " + String.valueOf(orders));
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            return new StrinResponse("Нет дохода");
        }


    }

    private OrderDTO toOrderDTO(Order old){
        return  new OrderDTO(
                toCustomerDto(old.getCustomer()),
                old.getStatus(),
                old.getTotalCost(),
                old.getCompletionDate(),
                toListBooks(old.getBooks())
        );
    }

    private List<BookOrderDTO> toListBooks(List<Book> old){
        return old.stream().map(this::toBookOrderDTO).toList();
    }

    private CustomerDTO toCustomerDto(Customer old){
        return new CustomerDTO(old.getName(), old.getSurname(), old.getEmail());
    }

    private BookOrderDTO toBookOrderDTO(Book old){
        return new BookOrderDTO(
                old.getTitle(),
                old.getPrice(),
                old.getGenre(),
                toAuthorDTO(old.getAuthor())
        );
    }

    private AuthorDTO toAuthorDTO(Author old){
        return new AuthorDTO(old.getName(), old.getSurname(), old.getPaternal());
    }

    private Customer toCustomer(CustomerDTO old){
        return new Customer(old.getName(), old.getSurname(), old.getEmail());
    }






}

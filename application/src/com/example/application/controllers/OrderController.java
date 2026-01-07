package com.example.application.controllers;

import com.example.application.exceptions.OrderCanNotBeCreated;
import com.example.application.model.Order;
import com.example.application.model.types.OrderSorting;
import com.example.application.model.types.OrderStatus;
import com.example.application.services.BookShopFacade;
import com.example.custom_annotations.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Inject
public class OrderController {
    @Inject
    private BookShopFacade bookshop;

//    public OrderController(BookShopFacade bookshop) {
//        this.bookshop = bookshop;
//    }

    String getOrderTypes(){
        return "1. Цена (по возрастанию)\n2. Цена (по убыванию)" +
                "3. Дата (по возрастанию)\n4. Дата (по убыванию)" +
                "5. Завершенные\n6. Незавершенные\n7. Отмененные";

    }

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

    List<Order> getAllOrders(String type){
        OrderSorting sorting = getOrderSorting(type);
        List<Order> orders = bookshop.getSortedOrders(sorting);

        return orders;

    }

    List<Order> displayOrdersInDiapazon(String firstDate, String secondDate, String type){
        // yyyy-MM-dd
        try {
            LocalDate first = LocalDate.parse(firstDate);
            LocalDate second = LocalDate.parse(secondDate);
            OrderSorting sorting = getOrderSorting(type);
            List<Order> orders = bookshop.getDoneOrdersInDiapazon(first,second, sorting);


            return orders;
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            return null;
        }


    }

    int displayOrderAmountInDiapazon(String firstDate, String secondDate){
        try {
            LocalDate first = LocalDate.parse(firstDate);
            LocalDate second = LocalDate.parse(secondDate);
            int orders = bookshop.getOrdersAmountInDiapazon(first,second);

            return orders;
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }


    }

    double displayIncomeInDiapazon(String firstDate, String secondDate){
        try {
            LocalDate first = LocalDate.parse(firstDate);
            LocalDate second = LocalDate.parse(secondDate);
            double orders = bookshop.getIncomeInDiapazon(first, second);

            return orders;
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }


    }

    String getOrderDetails(Order order){
        return bookshop.getOrderDetails(order);
    }

    Boolean deleteOrder(Order order){
        return bookshop.removeOrder(order);
    }

    ArrayList<Integer> createOrder(Order order){
        return bookshop.createOrder(order);
    }

    public Boolean changeOrderStatus(Order order, String bookTitle){
        if (order.getStatus() == OrderStatus.NEW){
            order.checkUpdateByBook(bookTitle);
            bookshop.saveOrder(order);
            return order.getStatus() == OrderStatus.DONE;
        }
        return false;
    }



    public Order getOrderById(String id_){
        try {
            int id = Integer.parseInt(id_);
            return bookshop.getOrderById(id);

        }catch (NumberFormatException e){
            return null;
        }

    }




}

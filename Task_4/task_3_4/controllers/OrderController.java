package task_3_4.controllers;

import task_3_4.model.Order;
import task_3_4.model.types.OrderSorting;
import task_3_4.model.types.OrderStatus;
import task_3_4.services.BookShopFacade;

import java.time.LocalDate;
import java.util.List;

public class OrderController {
    private BookShopFacade bookshop;

    public OrderController(BookShopFacade bookshop) {
        this.bookshop = bookshop;
    }

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
        LocalDate first = LocalDate.parse(firstDate);
        LocalDate second = LocalDate.parse(secondDate);
        OrderSorting sorting = getOrderSorting(type);
        List<Order> orders = bookshop.getDoneOrdersInDiapazon(first,second, sorting);

        return orders;

    }

    int displayOrderAmountInDiapazon(String firstDate, String secondDate){
        LocalDate first = LocalDate.parse(firstDate);
        LocalDate second = LocalDate.parse(secondDate);
        int orders = bookshop.getOrdersAmountInDiapazon(first,second);

        return orders;

    }

    double displayIncomeInDiapazon(String firstDate, String secondDate){
        LocalDate first = LocalDate.parse(firstDate);
        LocalDate second = LocalDate.parse(secondDate);
        double orders = bookshop.getIncomeInDiapazon(first, second);

        return orders;

    }

    String getOrderDetails(Order order){
        return bookshop.getOrderDetails(order);
    }

    Boolean deleteOrder(Order order){
        return bookshop.removeOrder(order);
    }

    Boolean createOrder(Order order){
        return bookshop.createOrder(order);
    }

    public Boolean changeOrderStatus(Order order, String bookTitle){
        if (order.getStatus() == OrderStatus.NEW){
            System.out.println("here");
            order.checkUpdateByBook(bookTitle);
            return order.getStatus() == OrderStatus.DONE;
        }
        return false;
    }



}

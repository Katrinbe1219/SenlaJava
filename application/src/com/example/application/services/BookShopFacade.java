package com.example.application.services;

import com.example.application.exceptions.OrderCanNotBeCreated;
import com.example.application.model.Book;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.model.Request;
import com.example.application.model.types.BookStatus;
import com.example.application.model.types.OrderSorting;
import com.example.application.model.types.OrderStatus;
import com.example.application.repositories.OrderRepository;
import com.example.application.repositories.RequestRepository;
import com.example.custom_annotations.Inject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Inject
public class BookShopFacade {
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private RequestRepository requestRepository;

//    public BookShopFacade(OrderRepository orderRepository,  RequestRepository requestRepository) {
//        this.orderRepository = orderRepository;
//        this.requestRepository = requestRepository;
//    }

    public ArrayList<Integer> createOrder(Order order){
        boolean checking = false;
        ArrayList<Integer> new_ids = new ArrayList<>();

        for(Book book: order.getBooks()){
            if (book.getStatus() == BookStatus.OUT_OF_STOCK){
                checking = true;

                int new_id = requestRepository.getCurrentMaxRequestId() + 1;
                requestRepository.add(new Request( new_id,book, order));
                requestRepository.incrementMaxRequestId();
                new_ids.add(new_id);
            }
        }

        if (!checking){
            order.setStatus(OrderStatus.DONE);
            order.setCompletionDate(LocalDate.now());

        }
        orderRepository.addOrder(order);
        if (order.getStatus() == OrderStatus.DONE){ return null;}
        else {return new_ids;}

    }


    public boolean removeOrder(Order order){
        List<Order> orders = orderRepository.getOrders();
        for (Order o: orders){
            if (o.getStatus() != OrderStatus.DONE && o.equals(order)){
                o.setStatus(OrderStatus.CANCELLED);
                return true;


            }
        }
        return false;
    }

    public String getOrderDetails(Order order){
        if (order != null) return order.toString();
        else return "Заказ не существует, создайте заказ";

    }

    public List<Order> getSortedOrders(OrderSorting sortingType){
        List<Order> orders = orderRepository.getOrders();
        return switch (sortingType) {
            case DONE -> orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .toList();
            case CANCELLED -> orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.CANCELLED)
                    .toList();
            case NEW -> orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.NEW)
                    .toList();
            case PRICE_UP -> orders.stream()
                    .sorted(Comparator.comparing(Order::getTotalCost))
                    .toList();
            case PRICE_DOWN -> orders.stream()
                    .sorted(Comparator.comparing(Order::getTotalCost).reversed())
                    .toList();
            case DATE_UP -> orders.stream()
                    .filter(p -> p.getStatus()== OrderStatus.DONE)
                    .sorted(Comparator.comparing(Order::getCompletionDate))
                    .toList();
            case DATE_DOWN -> orders.stream()
                    .filter(p -> p.getStatus()== OrderStatus.DONE)
                    .sorted(Comparator.comparing(Order::getCompletionDate).reversed())
                    .toList();
            default -> orders.stream().toList();
        };
    }

    public List<Order> getDoneOrdersInDiapazon(LocalDate start, LocalDate end, OrderSorting sortingType){
        List<Order> orders = orderRepository.getOrders();
        if (sortingType == OrderSorting.PRICE_UP){
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .sorted(Comparator.comparing(Order::getTotalCost))
                    .toList();
        } else if (sortingType == OrderSorting.PRICE_DOWN){
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .sorted(Comparator.comparing(Order::getTotalCost).reversed())
                    .toList();
        } else if (sortingType == OrderSorting.DATE_UP){
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .sorted(Comparator.comparing(Order::getCompletionDate))
                    .toList();
        } else if (sortingType == OrderSorting.DATE_DOWN){
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .sorted(Comparator.comparing(Order::getCompletionDate))
                    .toList();
        }else{
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .toList();
        }


    }

    public Integer getOrdersAmountInDiapazon(LocalDate start, LocalDate end){
        List<Order> orders = orderRepository.getOrders();
        return orders.stream()
                .filter(p -> p.getStatus() == OrderStatus.DONE)
                .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                .toList().size();


    }

    public Double getIncomeInDiapazon(LocalDate start, LocalDate end){
        double amount = 0;
        List<Order> orders = orderRepository.getOrders();
        for (Order order: orders){
            if (order.getStatus() == OrderStatus.DONE){

                if (order.getCompletionDate().isBefore(end) && order.getCompletionDate().isAfter(start)){
                    amount += order.getTotalCost();
                }
            }
        }

        return amount;
    }

    public int getMaxCurrentId(){
        return orderRepository.getCurrentMaxId();
    }

    public void incrementMaxId(){
        orderRepository.incrementMaxId();
    }

    public Order getOrderById(int id){
        List<Order> orders=  orderRepository.getOrders();
        for (Order o : orders){
            if (o.getId() == id ){
                return o;
            }
        }

        return null;
    }








}

package com.example.application.services;

import com.example.application.dao.CustomerImplemenation;
import com.example.application.dao.OrderBooksImplementation;
import com.example.application.dao.OrderImplementation;
import com.example.application.dao.RequestImplementation;
import com.example.application.errors.CanNotMakeExecution;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Inject
public class BookShopFacade {


    @Inject
    Connection connection;
    @Inject
    private OrderImplementation orderDao;
    @Inject
    private OrderBooksImplementation obDao;
    @Inject
    private CustomerImplemenation customerDao;
    @Inject
    private RequestImplementation requestDao;



    // ПРЕДПОЛАГАЕТСЯ, что здесь существует маленькая база данных, ничтожное количество заказов =>
    // получаем список заказов, а потом проходясь по этому списку берем из бд для каждого из них книги, когда получаем все orders
    // тем более что одно connection, значит данная программа не предназначена для каких-то параллельных запросов для огромного числа пользователей


    public ArrayList<Integer> createOrder(Order order) throws CanNotMakeExecution {
        boolean checking = false;
        ArrayList<Integer> new_ids = new ArrayList<>();
        ArrayList<Integer> book_ids = new ArrayList<>();

        for(Book book: order.getBooks()){
            if (book.getStatus() == BookStatus.OUT_OF_STOCK){
                checking = true;
                book_ids.add(book.getId());
            }
        }

        if (!checking){
            order.setStatus(OrderStatus.DONE);
            order.setCompletionDate(LocalDate.now());

        }
        try {
            // transaction---------------------------

            connection.setAutoCommit(false);

            customerDao.save(order.getCustomer());
            orderDao.save(order);
            obDao.addOrderBooks(order.getBooks(), order.getId());
            requestDao.insertMany(book_ids,order.getId() );
            connection.commit();
        }
        catch (SQLException e){

            try {
                connection.rollback();
            }catch (SQLException e1){
                System.out.println("rollback не работает " + e1.getMessage());
            }

            throw new CanNotMakeExecution("Проблема создания заказа SQL " + e.getMessage());

        }
        catch (Exception e1){
            throw new CanNotMakeExecution("Проблема создания заказа CAnNotMakeException or others : " + e1.getMessage());
        }
        finally {

            try{
                connection.setAutoCommit(true);
            }catch (SQLException e){
                System.out.println("Проблема с автокоммитом :" + e.getMessage());
            }
        }

        if (order.getStatus() == OrderStatus.DONE){ return null;}
        else {return new_ids;}

    }


    public boolean removeOrder(Order order){
        try {
                order.setStatus(OrderStatus.CANCELLED);
                orderDao.save(order);
                return true;



        } catch (CanNotMakeExecution e){
            System.out.println("Проблема при получении заказов SQl type: " + e.getMessage());
            return false;
        }catch (Exception e){
            System.out.println("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return false;
        }


    }

    public void saveOrder(Order order){
        try {
            orderDao.save(order);

        }catch (CanNotMakeExecution e){
            System.out.println("Проблема при сохранении заказа" + e.getMessage());
        }
    }
    public String getOrderDetails(Order order){
        if (order != null) return order.toString();
        else return "Заказ не существует, создайте заказ";

    }

    public List<Order> getSortedOrders(OrderSorting sortingType){
        try {
            List<Order> orders = orderDao.getOrders();
            if (orders == null || orders.size() ==0 ){
                return null;
            }
            fillOrdersWithOBooks(orders);

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
        catch (CanNotMakeExecution e){
            System.out.println("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            System.out.println("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }

    }

    public List<Order> getDoneOrdersInDiapazon(LocalDate start, LocalDate end, OrderSorting sortingType){
        try {
            List<Order> orders = orderDao.getOrders();
            fillOrdersWithOBooks(orders);
            if (sortingType == OrderSorting.PRICE_UP) {
                return orders.stream()
                        .filter(p -> p.getStatus() == OrderStatus.DONE)
                        .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                        .sorted(Comparator.comparing(Order::getTotalCost))
                        .toList();
            }
            else if (sortingType == OrderSorting.PRICE_DOWN) {
                return orders.stream()
                        .filter(p -> p.getStatus() == OrderStatus.DONE)
                        .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                        .sorted(Comparator.comparing(Order::getTotalCost).reversed())
                        .toList();
            } else if (sortingType == OrderSorting.DATE_UP) {
                return orders.stream()
                        .filter(p -> p.getStatus() == OrderStatus.DONE)
                        .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                        .sorted(Comparator.comparing(Order::getCompletionDate))
                        .toList();
            } else if (sortingType == OrderSorting.DATE_DOWN) {
                return orders.stream()
                        .filter(p -> p.getStatus() == OrderStatus.DONE)
                        .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                        .sorted(Comparator.comparing(Order::getCompletionDate))
                        .toList();
            }
            else {
                return orders.stream()
                        .filter(p -> p.getStatus() == OrderStatus.DONE)
                        .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                        .toList();
            }
        }catch(CanNotMakeExecution e){
                System.out.println("Проблема при получении заказов SQl type: " + e.getMessage());
                return null;
        }catch(Exception e){
            System.out.println("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
            }

        }


    public Integer getOrdersAmountInDiapazon(LocalDate start, LocalDate end){
        try{
            List<Order> orders = orderDao.getOrders();
            fillOrdersWithOBooks(orders);
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .toList().size();
        } catch (CanNotMakeExecution e){
            System.out.println("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            System.out.println("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }



    }

    public Double getIncomeInDiapazon(LocalDate start, LocalDate end){
        double amount = 0;
        try {
            List<Order> orders = orderDao.getOrders();
            fillOrdersWithOBooks(orders);
            for (Order order: orders){
                if (order.getStatus() == OrderStatus.DONE){

                    if (order.getCompletionDate().isBefore(end) && order.getCompletionDate().isAfter(start)){
                        amount += order.getTotalCost();
                    }
                }
            }

            return amount;

        } catch (CanNotMakeExecution e){
            System.out.println("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            System.out.println("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }
    }



    public Order getOrderById(int id){
        try{
            List<Order> orders=  orderDao.getOrders();
            for (Order o : orders){
                if (o.getId() == id ){
                    fillOrderWithBooks(o);
                    return o;
                }
            }

            return null;
        }catch (CanNotMakeExecution e){
            System.out.println("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            System.out.println("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }


    }

    private void fillOrdersWithOBooks(List<Order> orders){
        for (Order order : orders){
            List<Book> books = obDao.getOrdersBook(order.getId());
            for (Book book : books){
                order.addBook(book);
            }
        }
    }

    private void fillOrderWithBooks(Order order){
        List<Book> books = obDao.getOrdersBook(order.getId());
        for (Book book : books){
            order.addBook(book);
        }


    }








}

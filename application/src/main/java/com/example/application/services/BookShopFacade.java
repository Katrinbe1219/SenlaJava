package com.example.application.services;

import com.example.application.dao.CustomerImplemenation;
import com.example.application.dao.OrderBooksImplementation;
import com.example.application.dao.OrderImplementation;
import com.example.application.dao.RequestImplementation;
import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.types.BookStatus;
import com.example.application.model.types.OrderSorting;
import com.example.application.model.types.OrderStatus;
import com.example.custom_applications.Inject;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    public ArrayList<Integer> createOrder(Order order, Logger logger) throws CanNotMakeExecution {
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

            customerDao.save(order.getCustomer(), logger);
            orderDao.save(order, logger);
            obDao.addOrderBooks(order.getBooks(), order.getId(), logger);
            requestDao.insertMany(book_ids,order.getId() , logger);
            connection.commit();
        }
        catch (SQLException e){

            try {
                connection.rollback();
            }catch (SQLException e1){
                logger.error("rollback не работает " + e1.getMessage());
            }

            throw new CanNotMakeExecution("Проблема создания заказа SQL " + e.getMessage());

        }
        catch (Exception e1){
            logger.error("Проблема в BookShopFacade createOrder: " + e1.getMessage());
            throw new CanNotMakeExecution("Проблема создания заказа CAnNotMakeException or others : " + e1.getMessage());
        }
        finally {

            try{
                connection.setAutoCommit(true);
            }catch (SQLException e){
                logger.error("Проблема с автокоммитом :" + e.getMessage());
            }
        }

        if (order.getStatus() == OrderStatus.DONE){ return null;}
        else {return new_ids;}

    }


    public boolean removeOrder(Order order,Logger logger){
        try {
                order.setStatus(OrderStatus.CANCELLED);
                orderDao.save(order, logger);
                return true;



        } catch (CanNotMakeExecution e){
            logger.error("Проблема при получении заказов SQl type: " + e.getMessage());
            return false;
        }catch (Exception e){
            logger.error("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return false;
        }


    }

    public void saveOrder(Order order,Logger logger){
        try {
            orderDao.save(order, logger);

        }catch (CanNotMakeExecution e){
            logger.error("Проблема при сохранении заказа" + e.getMessage());
        }
    }
    public String getOrderDetails(Order order){
        if (order != null) return order.toString();
        else return "Заказ не существует, создайте заказ";

    }

    public List<Order> getSortedOrders(OrderSorting sortingType,Logger logger){
        try {
            Optional<List<Order>> orders_;
            List<Order> orders;

            return switch (sortingType) {
                case DONE ->{
                    orders_ = orderDao.getOrdersSorted("status_D", "", logger);
                    if (orders_.isEmpty()){ yield  null;}
                    orders = orders_.get();
                    fillOrdersWithOBooks(orders,false, logger);
                    yield orders;
                }
                case CANCELLED -> {
                    orders_ = orderDao.getOrdersSorted("status_C", "", logger);
                    if (orders_.isEmpty()){ yield  null;}
                    orders = orders_.get();
                    fillOrdersWithOBooks(orders,false, logger);
                    yield orders;
                }
                case NEW -> {
                    orders_ = orderDao.getOrdersSorted("status_N", "", logger);
                    if (orders_.isEmpty()){ yield  null;}
                    orders = orders_.get();
                    fillOrdersWithOBooks(orders,false, logger);
                    yield orders;
                }
                case PRICE_UP -> {
                    orders_ = orderDao.getOrdersSorted("total_cost", "ASC", logger);
                    if (orders_.isEmpty()){ yield  null;}
                    orders = orders_.get();
                    fillOrdersWithOBooks(orders,false, logger);
                    yield orders;
                }
                case PRICE_DOWN -> {
                    orders_ = orderDao.getOrdersSorted("total_cost", "DESC", logger);
                    if (orders_.isEmpty()){ yield  null;}
                    orders = orders_.get();
                    fillOrdersWithOBooks(orders,false, logger);
                    yield orders;
                }
                case DATE_UP -> {
                    orders_ = orderDao.getOrdersSorted("completion_date", "ASC", logger);
                    if (orders_.isEmpty()){ yield  null;}
                    orders = orders_.get();
                    fillOrdersWithOBooks(orders,false, logger);
                    yield orders;
                }
                case DATE_DOWN -> {
                    orders_ = orderDao.getOrdersSorted("completion_date", "DESC", logger);
                    if (orders_.isEmpty()){ yield  null;}
                    orders = orders_.get();
                    fillOrdersWithOBooks(orders,false, logger);
                    yield orders;
                }
            };
        }
        catch (CanNotMakeExecution e){
            logger.error("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            logger.error("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }

    }

    public List<Order> getDoneOrdersInDiapazon(LocalDate start, LocalDate end, OrderSorting sortingType,Logger logger){
        try {
            Optional<List<Order>> orders_;
            List<Order> orders = orderDao.getOrders(logger);
            fillOrdersWithOBooks(orders,false, logger);

            if (sortingType == OrderSorting.PRICE_UP) {
                orders_ = orderDao.getSortedDoneOrders(start, end, "total_cost", "ASC", logger);
                if (orders_.isEmpty()) {return null;}
                orders = orders_.get();
                fillOrdersWithOBooks(orders,true, logger);
                return orders;

            }
            else if (sortingType == OrderSorting.PRICE_DOWN) {
                orders_ = orderDao.getSortedDoneOrders(start, end, "total_cost", "DESC", logger);
                if (orders_.isEmpty()) {return null;}
                orders = orders_.get();
                fillOrdersWithOBooks(orders,true, logger);
                return orders;

            } else if (sortingType == OrderSorting.DATE_UP) {
                orders_ = orderDao.getSortedDoneOrders(start, end, "completion_date", "ASC", logger);
                if (orders_.isEmpty()) {return null;}
                orders = orders_.get();
                fillOrdersWithOBooks(orders,false, logger);
                return orders;

            } else if (sortingType == OrderSorting.DATE_DOWN) {
                orders_ = orderDao.getSortedDoneOrders(start, end, "completion_date", "DESC", logger);
                if (orders_.isEmpty()) {return null;}
                orders = orders_.get();
                fillOrdersWithOBooks(orders,false, logger);
                return orders;
            }
            else {
                orders_ = orderDao.getSortedDoneOrders(start, end, "order_id", "ASC", logger);
                if (orders_.isEmpty()) {return null;}
                orders = orders_.get();
                fillOrdersWithOBooks(orders,false, logger);
                return orders;
            }
        }catch(CanNotMakeExecution e){
                logger.error("Проблема при получении заказов SQl type: " + e.getMessage());
                return null;
        }catch(Exception e){
            logger.error("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
            }

        }


    public Integer getOrdersAmountInDiapazon(LocalDate start, LocalDate end,Logger logger){
        try{
            Optional<List<Order> >orders_ = orderDao.getOrdersInDiapazon(start, end, logger);
            if (orders_.isEmpty()) {
                return 0;
            }
            List<Order> orders = orders_.get();
            return orders.size();
        } catch (CanNotMakeExecution e){
            logger.error("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            logger.error("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }



    }

    public Double getIncomeInDiapazon(LocalDate start, LocalDate end,Logger logger){
        double amount = 0;
        try {
            Optional<List<Order> >orders_ = orderDao.getOrdersInDiapazon(start, end, logger);
            if (orders_.isEmpty()) {
                return 0D;
            }
            List<Order> orders = orders_.get();
            fillOrdersWithOBooks(orders, false, logger);
            for (Order order: orders){
                        amount += order.getTotalCost();
            }

            return amount;

        } catch (CanNotMakeExecution e){
            logger.error("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            logger.error("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }
    }



    public Order getOrderById(int id,Logger logger){
        try{
            List<Order> orders=  orderDao.getOrders(logger);
            for (Order o : orders){
                if (o.getId() == id ){
                    fillOrderWithBooks(o, logger);
                    return o;
                }
            }

            return null;
        }catch (CanNotMakeExecution e){
           logger.error("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            logger.error("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }


    }

    private void fillOrdersWithOBooks(List<Order> orders, boolean condition, Logger logger){
        // ошибка перейдет в другую функцию, где идет обработка и логирование
        for (Order order : orders){
            List<Book> books = obDao.getOrdersBook(order.getId(), logger);
            for (Book book : books){
                if (condition){
                    order.addBook(book, true);
                }else {
                    order.addBook(book);
                }

            }
        }
    }

    private void fillOrderWithBooks(Order order, Logger logger){
        // ошибка перейдет в другую функцию, где идет обработка и логиро
        List<Book> books = obDao.getOrdersBook(order.getId(), logger);
        for (Book book : books){
            order.addBook(book);
        }


    }








}

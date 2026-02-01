package com.example.application.services;

import com.example.application.dao.CustomerImplemenation;
import com.example.application.dao.OrderBooksImplementation;
import com.example.application.dao.OrderImplementation;
import com.example.application.dao.RequestImplementation;
import com.example.application.errors.CanNotMakeExecution;
import com.example.application.hibernate.HibernateUtils;
import com.example.application.hibernate.OrderHibImplementation;
import com.example.application.hibernate.RequestHibImpl;
import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.types.BookStatus;
import com.example.application.model.types.OrderSorting;
import com.example.application.model.types.OrderStatus;
import com.example.custom_applications.Inject;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;


import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BookShopFacade {

    private RequestHibImpl requestHibImpl;

    private OrderHibImplementation orderHibImpl;

    public BookShopFacade(RequestHibImpl requestHibImpl, OrderHibImplementation orderHibImpl) {
        this.requestHibImpl = requestHibImpl;
        this.orderHibImpl = orderHibImpl;
    }


    // ПРЕДПОЛАГАЕТСЯ, что здесь существует маленькая база данных, ничтожное количество заказов =>
    // получаем список заказов, а потом проходясь по этому списку берем из бд для каждого из них книги, когда получаем все orders
    // тем более что одно connection, значит данная программа не предназначена для каких-то параллельных запросов для огромного числа пользователей


    public ArrayList<Integer> createOrder(Order order, Logger logger) throws CanNotMakeExecution {
        boolean checking = false;
        ArrayList<Integer> new_ids = new ArrayList<>();
        ArrayList<Book> books = new ArrayList<>();

        for(Book book: order.getBooks()){
            if (book.getStatus() == BookStatus.OUT_OF_STOCK){
                checking = true;
                books.add(book);
            }
        }

        if (!checking){
            order.setStatus(OrderStatus.DONE);
            order.setCompletionDate(LocalDate.now());

        }

        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {

            orderHibImpl.save(order, logger, session, tx);
            requestHibImpl.insertMany(books, order, logger, session);
            tx.commit();

        }

        catch (Exception e1){
            tx.rollback();
            logger.error("Проблема в BookShopFacade createOrder: " + e1.getMessage());
            throw new CanNotMakeExecution("Проблема создания заказа CAnNotMakeException or others : " + e1.getMessage());
        }
        finally {
                session.close();
        }

        if (order.getStatus() == OrderStatus.DONE){ return null;}
        else {return new_ids;}

    }


    public boolean removeOrder(Order order,Logger logger){
        try {
                order.setStatus(OrderStatus.CANCELLED);
                orderHibImpl.update(order, logger);
                return true;



        } catch (CanNotMakeExecution e){
            logger.error("Проблема при получении заказов SQl type: " + e.getMessage());
            return false;
        }catch (Exception e){
            logger.error("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return false;
        }


    }


    public void updateOrders(List<Order> order, Logger logger){
        try {
            orderHibImpl.update(order, logger);

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
            List<Order> orders;

            return switch (sortingType) {
                case DONE ->{
                    orders = orderHibImpl.getOrdersSorted(OrderStatus.DONE,  logger);
                    orders.forEach(Order::countTotalCost);
                    yield orders;
                }
                case CANCELLED -> {
                    orders = orderHibImpl.getOrdersSorted(OrderStatus.CANCELLED,  logger);
                    orders.forEach(Order::countTotalCost);
                    yield orders;
                }
                case NEW -> {
                    orders = orderHibImpl.getOrdersSorted(OrderStatus.NEW,  logger);
                    orders.forEach(Order::countTotalCost);
                    yield orders;
                }
                case PRICE_UP -> {
                    orders = orderHibImpl.findAll( logger);
                    orders.forEach(Order::countTotalCost);
                    orders = orders.stream().sorted(Comparator.comparing(Order::getTotalCost)).toList();
                    yield orders;
                }
                case PRICE_DOWN -> {
                    orders = orderHibImpl.findAll( logger);
                    orders.forEach(Order::countTotalCost);
                    orders = orders.stream().sorted(Comparator.comparing(Order::getTotalCost).reversed()).toList();
                    yield orders;
                }
                case DATE_UP -> {
                    orders = orderHibImpl.getOrdersSorted("completionDate", false, logger);
                    orders.forEach(Order::countTotalCost);
                    yield orders;
                }
                case DATE_DOWN -> {
                    orders = orderHibImpl.getOrdersSorted("completionDate", true, logger);
                    orders.forEach(Order::countTotalCost);
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
            List<Order> orders;

            if (sortingType == OrderSorting.PRICE_UP) {
                orders = orderHibImpl.getSortedDoneOrders(start, end,  logger);
                orders.forEach(Order::countTotalCost);
                orders = orders.stream().sorted(
                        Comparator.comparing(Order::getTotalCost)
                ).toList();
                return orders;

            }
            else if (sortingType == OrderSorting.PRICE_DOWN) {
                orders = orderHibImpl.getSortedDoneOrders(start, end,  logger);
                orders.forEach(Order::countTotalCost);
                orders = orders.stream().sorted(
                        Comparator.comparing(Order::getTotalCost).reversed()
                ).toList();
                return orders;

            } else if (sortingType == OrderSorting.DATE_UP) {
                orders = orderHibImpl.getSortedDoneOrders(start, end, false, logger);
                orders.forEach(Order::countTotalCost);
                return orders;

            } else if (sortingType == OrderSorting.DATE_DOWN) {
                orders = orderHibImpl.getSortedDoneOrders(start, end,  true, logger);
                orders.forEach(Order::countTotalCost);
                return orders;
            }
            else {
                orders = orderHibImpl.getSortedDoneOrders(start, end,  logger);
                orders.forEach(Order::countTotalCost);
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
           List<Order> orders_ = orderHibImpl.getOrdersInDiapazon(start, end, logger);
            return orders_.size();

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
            List<Order>  orders = orderHibImpl.getOrdersInDiapazon(start, end, logger);
            if (orders.isEmpty()) {
                return 0D;
            }

            for (Order order: orders){
                    order.countTotalCost();
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

            Order order = orderHibImpl.getById(id, logger);
            if (order == null)  return null;
            order.countTotalCost();
            return order;

        }catch (CanNotMakeExecution e){
           logger.error("Проблема при получении заказов SQl type: " + e.getMessage());
            return null;
        }catch (Exception e){
            logger.error("Проблема при получении заказов nonSQl type: " + e.getMessage());
            return null;
        }


    }










}

package com.example.application.repositories;

import com.example.application.model.BookShop;
import com.example.application.model.Order;
import com.example.custom_applications.Inject;


import java.util.List;

@Inject
public class OrderRepository {
    @Inject
    private BookShop bookShop;

//    public OrderRepository(BookShop bookShop) {
//        this.bookShop = bookShop;
//    }

    public void addOrder(Order order){
        bookShop.addOrder(order);
    }

    public List<Order> getOrders(){
        return bookShop.getOrders();
    }

    public int getCurrentMaxId(){
        return bookShop.getCountAllOrder();
    }

    public void incrementMaxId(){
        bookShop.incrementAllOrder();
    }

    public void checkMaxId(int num){
        bookShop.checkMaxCountOrder(num);
    }




}

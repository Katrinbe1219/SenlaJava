package task_3_4.repositories;

import task_3_4.model.Book;
import task_3_4.model.BookShop;
import task_3_4.model.Order;


import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private BookShop bookShop;

    public OrderRepository(BookShop bookShop) {
        this.bookShop = bookShop;
    }

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

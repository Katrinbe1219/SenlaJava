package task_3_4.repositories;

import task_3_4.model.Book;
import task_3_4.model.BookShop;
import task_3_4.model.Order;


import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private BookShop bookShop;

    public OrderRepository() {
        this.bookShop = new BookShop();
    }

    public void addOrder(Order order){
        bookShop.addOrder(order);
    }

    public List<Order> getOrders(){
        return bookShop.getOrders();
    }


}

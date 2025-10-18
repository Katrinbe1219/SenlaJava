package task_3_4.warehouse_work;

import task_3_4.books.IBook;
import task_3_4.order.Order;

public class Request {
    private IBook book;
    private Order order;


    public Request(IBook book, Order order){
        System.out.println("Создан запрос на книгу " + book.getTitle());
        this.book = book;
        this.order = order;
    }

    Order getOrder(){
        return this.order;
    }

    IBook getBook(){
        return this.book;
    }
}

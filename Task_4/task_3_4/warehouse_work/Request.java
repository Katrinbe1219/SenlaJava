package task_3_4.warehouse_work;

import task_3_4.books.Book;
import task_3_4.order.Order;

public class Request {
    private Book book;
    private Order order;


    public Request(Book book, Order order){
        System.out.println("Создан запрос на книгу " + book.getTitle());
        this.book = book;
        this.order = order;
    }

    Order getOrder(){
        return this.order;
    }

    Book getBook(){
        return this.book;
    }
}

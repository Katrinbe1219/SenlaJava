package task_3_4.model;

import java.io.Serializable;
import java.util.Comparator;

public class Request implements Comparable<Request>, Serializable {
    private Book book;
    private Order order;
    int id;


    public Request(int id, Book book, Order order){
        this.id = id;
        this.book = book;
        this.order = order;
    }

    public Order getOrder(){
        return this.order;
    }

    public Book getBook(){
        return this.book;
    }

    public int getId(){
        return this.id;
    }

    public void setBook(Book book){
        this.book = book;
    }

    public void setOrder (Order order){
        this.order = order;
    }

    @Override
    public int compareTo(Request o) {
        return Comparator
                .comparing(Request::getId, Comparator.nullsFirst(Integer::compareTo))
                .thenComparing(r -> r.getBook().getId(), Comparator.nullsFirst(Integer::compareTo))
                .thenComparing(r -> r.getOrder().getId(), Comparator.nullsFirst(Integer::compareTo))
                .compare(this,o);
    }
}

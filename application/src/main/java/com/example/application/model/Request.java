package com.example.application.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Comparator;

@Entity(name = "requests")
public class Request implements Comparable<Request>, Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    int id;


    public Request(){}

    public Request(Book book, Order order) {
        this.book = book;
        this.order = order;
    }

    public void setId(int id){
        this.id = id;
    }

    public Integer getId(){
        return id;
    }

    public Order getOrder(){
        return this.order;
    }

    public Book getBook(){
        return this.book;
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

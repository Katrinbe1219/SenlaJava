package com.example.application.model;

import java.io.Serializable;
import java.util.Comparator;

public class Request implements Comparable<Request>, Serializable {
    private int book;
    private int order;
    int id;


    public Request(){}

    public void setId(int id){
        this.id = id;
    }

    public Integer getId(){
        return id;
    }
    public Integer getOrder(){
        return this.order;
    }

    public Integer getBook(){
        return this.book;
    }



    public void setBook(Integer book){
        this.book = book;
    }

    public void setOrder (Integer order){
        this.order = order;
    }

    @Override
    public int compareTo(Request o) {
        return Comparator
                .comparing(Request::getId, Comparator.nullsFirst(Integer::compareTo))
                .thenComparing(Request::getBook, Comparator.nullsFirst(Integer::compareTo))
                .thenComparing(Request::getOrder, Comparator.nullsFirst(Integer::compareTo))
                .compare(this,o);
    }
}

package com.example.application.model;


public class RequestResult {
    private String book;
    int id;


    public RequestResult(String book, int id){
        this.book = book;
        this.id = id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Integer getId(){
        return id;
    }


    public String getBook(){
        return this.book;
    }



    public void setBook(String book){
        this.book = book;
    }

    @Override
    public String toString() {
        return "RequestResult [book=" + book + ", id=" + id + "]";
    }




}


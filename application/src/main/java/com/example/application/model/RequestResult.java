package com.example.application.model;


public class RequestResult {
    private String book;
    Long id;


    public RequestResult(String book, Long id){
        this.book = book;
        this.id = id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
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
        return "RequestResult: [book=" + book + ", amount=" + id + "]";
    }




}


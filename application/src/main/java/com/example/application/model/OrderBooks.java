package com.example.application.model;

public class OrderBooks {
    int order_books_id;
    int book_id;
    int order_id;

    public OrderBooks(){}
    public void setBookID(int book_id) {
        this.book_id = book_id;
    }
    public void setOrderID(int order_id) {
        this.order_id = order_id;
    }
    public int getBookID() {
        return book_id;
    }
    public int getOrderID() {
        return order_id;
    }

    public void setOrderBookId(int order_books_id) {
        this.order_books_id = order_books_id;
    }
    public int getOrderBookId() {
        return order_books_id;
    }
}

package com.example.application.dto;

import java.util.List;

public class OrderCreateDto {
    CustomerDTO customer;
    List<String> books;

    public OrderCreateDto() {}

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }

    public List<String> getBooks(){
        return this.books;
    }
}

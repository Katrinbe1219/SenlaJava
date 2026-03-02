package com.example.application.dto;

import com.example.application.model.types.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public class OrderDTO {
    CustomerDTO customer;
    OrderStatus status;
    double totalCost;
    String completionDate;
    List<BookOrderDTO> books;

    public OrderDTO() {}

    public OrderDTO(CustomerDTO customer, OrderStatus status, double totalCost, LocalDate completionDate, List<BookOrderDTO> books){
        this.customer = customer;
        this.status = status;
        this.totalCost = totalCost;
        this.completionDate = String.valueOf(completionDate);
        this.books = books;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }
    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public double getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    public String getCompletionDate() {
        return completionDate;
    }
    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = String.valueOf(completionDate);
    }

    public List<BookOrderDTO> getBooks() {
        return books;
    }
    public void setBooks(List<BookOrderDTO> books) {
        this.books = books;
    }




}

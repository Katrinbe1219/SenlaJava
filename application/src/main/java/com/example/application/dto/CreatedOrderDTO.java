package com.example.application.dto;

import com.example.application.model.Book;
import com.example.application.model.Customer;
import com.example.application.model.Request;
import com.example.application.model.types.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude()
public class CreatedOrderDTO {
    List<ReceiveRequest> reqs;
    List<BookDTO> books;
    OrderStatus status;

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setReqs(List<ReceiveRequest> reqs) {
        this.reqs = reqs;
    }

    public void setBooks(List<BookDTO> books) {
        this.books = books;
    }



    //  для Jackson необходимо, чтобы все геттеры были прописаны
    public OrderStatus getStatus() {
        return status;
    }

    public List<ReceiveRequest> getReqs() {
        return reqs;
    }

    public List<BookDTO> getBooks() {
        return books;
    }

    public CreatedOrderDTO(){
    };
}

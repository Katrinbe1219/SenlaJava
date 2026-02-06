package com.example.application.dto;

import com.example.application.model.types.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public class OrderDTO {
    CustomerDTO customer;
    OrderStatus status;
    double totalCost;
    LocalDate completionDate;
    List<BookDTO> books;
}

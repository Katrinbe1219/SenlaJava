package com.example.application.controllers;

import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.Request;
import com.example.application.model.types.RequestSorting;
import com.example.application.repositories.RequestRepository;
import com.example.application.services.BookService;
import com.example.application.services.BookShopFacade;
import com.example.custom_annotations.Inject;

import java.util.List;

@Inject
public class RequestController {
    @Inject
    private BookService bookService;

//    public RequestController(BookService bookService) {
//        this.bookService = bookService;
//    }

    List<List<Object>> getAllRequests(String type){
        RequestSorting sorting = getRequestSorting(type);
        return bookService.getSortedRequests(sorting);
    }

    void deleteRequestByBook(Book book){
        bookService.cancellRequestsByBook(book);
    }

    void deleteRequestByOrder(Order order){
        bookService.cancellOrderRequests(order);
    }

    private RequestSorting getRequestSorting(String type){
        return switch (type){
            case "1" -> RequestSorting.ALPHABETICAL_UP;
            case "2" -> RequestSorting.ALPHABETICAL_DOWN;
            case "3" -> RequestSorting.AMOUNT_UP;
            case "4" -> RequestSorting.AMOUNT_DOWN;
            default -> RequestSorting.ALPHABETICAL_UP;
        };
    }

    String getRequestTypes(){
        return "1. По алфавиту (по возрастанию)\n2. По алфавиту (по убыванию)\n" +
                "3. По количеству (по возрастанию)\n4. По количеству (по убыванию)\n";
    }

    public String importRequest(String filename){
        return bookService.importRequest(filename);
    }

    public String exportRequest(String id){
        return bookService.exportRequest(id);
    }
}

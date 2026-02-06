package com.example.application.controllers;

import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.RequestResult;
import com.example.application.model.types.RequestSorting;
import com.example.application.services.BookService;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestController {

    private BookService bookService;

    public RequestController(BookService bookService) {
        this.bookService = bookService;
    }



    List<RequestResult> getAllRequests(String type, Logger logger){
        RequestSorting sorting = getRequestSorting(type);
        return bookService.getSortedRequests(sorting, logger);
    }

    void deleteRequestByBook(Book book, Logger logger){
        bookService.cancellRequestsByBook(book, logger);
    }

    void deleteRequestByOrder(Order order, Logger logger){
        bookService.cancellOrderRequests(order, logger);
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

//    public String importRequest(String filename){
//        return "";
//        //return bookService.importRequest(filename);
//    }
//
//    public String exportRequest(String id){
//        return bookService.exportRequest(id);
//    }
}

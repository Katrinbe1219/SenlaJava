package com.example.application.controllers;

import com.example.application.model.Order;
import com.example.application.model.types.RequestSorting;
import com.example.application.services.BookService;
import com.example.custom_applications.Inject;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Inject
public class RequestController {
    @Inject
    private BookService bookService;



    List<List<Object>> getAllRequests(String type, Logger logger){
        RequestSorting sorting = getRequestSorting(type);
        return bookService.getSortedRequests(sorting, logger);
    }

    void deleteRequestByBook(Integer book_id, Logger logger){
        bookService.cancellRequestsByBook(book_id, logger);
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

package com.example.application.controllers;


import com.example.application.model.RequestResult;
import com.example.application.model.types.RequestSorting;
import com.example.application.services.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private BookService bookService;
    private static  final Logger logger = LogManager.getLogger(RequestController.class.getName());

    public RequestController(BookService bookService) {
        this.bookService = bookService;
    }



    @GetMapping
    @PreAuthorize("hasAuthority('get_models')")
    List<RequestResult> getAllRequests(@RequestParam("type") String type){
        RequestSorting sorting = getRequestSorting(type);
        return bookService.getSortedRequests(sorting, logger);
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


}

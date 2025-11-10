package task_3_4.controllers;

import task_3_4.model.Book;
import task_3_4.model.Order;
import task_3_4.model.Request;
import task_3_4.model.types.RequestSorting;
import task_3_4.repositories.RequestRepository;
import task_3_4.services.BookService;
import task_3_4.services.BookShopFacade;

import java.util.List;

public class RequestController {
    private BookService bookService;

    public RequestController(BookService bookService) {
        this.bookService = bookService;
    }

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
}

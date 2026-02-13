package com.example.application.controllers;

import com.example.application.dto.BookDTO;
import com.example.application.model.Book;
import com.example.application.model.types.BookSorting;
import com.example.application.model.types.LongLiedBookSorting;
import com.example.application.services.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private BookService bookService;
    private static final Logger logger = LogManager.getLogger();

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public  List<BookDTO> displayAllBooks(){
        List<BookDTO> result =   bookService.getAllBooks(logger);
        return result;

    }




    @GetMapping("/sorted")
    public List<BookDTO> displaySortedBooks(@RequestParam("type")  String type){
        BookSorting sorting= getBookType(type);
        return bookService.getSortedBooks(sorting,logger);
    }




    @GetMapping("/longLied")
    public List<BookDTO> displayLongLiedBooks(@RequestParam("type") String type, @RequestParam("numberOfMonth")  int numberOfMonth){
        LongLiedBookSorting sorting= getLongLiedBookType(type);
        return bookService.getLongLiedBooks(sorting, numberOfMonth, logger);
    }



    @GetMapping(value = "check", produces = "text/plain; charset=UTF-8")
    public String checkBook(@RequestParam("book") String book){
        boolean checking = bookService.checkBook(book, logger);
        if (checking) {
            return "Книга в наличии";
        }else{
            return "Книга не в наличии";
        }
    }


    @GetMapping(value = "/description", produces = "text/plain; charset=UTF-8")
    public String displayBookDescription(@RequestParam("book") String bookName){
        String description = bookService.getBookDescription(bookName, logger);
        if (description == null) {
            return "Не было найдено";
        }else{
            return description;
        }
    }



    BookSorting getBookType(String type){
        return switch (type) {
            case "1" -> BookSorting.ALPHABETICAL_UP;
            case "2" -> BookSorting.ALPHABETICAL_DOWN;
            case "3" -> BookSorting.INSTOCK;
            case "4" -> BookSorting.PRICE_UP;
            case "5" -> BookSorting.PRICE_DOWN;
            case "6" -> BookSorting.DATE_UP;
            case "7" -> BookSorting.DATE_DOWN;
            default -> BookSorting.ALL;
        };

    }

    LongLiedBookSorting getLongLiedBookType(String type){
        return switch (type){
            case "1" -> LongLiedBookSorting.DATE_UP;

            case "2" -> LongLiedBookSorting.DATE_DOWN;
            case "3" -> LongLiedBookSorting.PRICE_UP;
            case "4" -> LongLiedBookSorting.PRICE_DOWN;
            default -> LongLiedBookSorting.NONE;
        };

    }


}

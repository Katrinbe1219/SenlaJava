package com.example.application.controllers;

import com.example.application.model.Book;
import com.example.application.model.types.BookSorting;
import com.example.application.model.types.LongLiedBookSorting;
import com.example.application.services.BookService;
import com.example.custom_applications.Inject;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Inject
public class BookController {
    @Inject
    private BookService bookService;



    public Optional<List<Book>> displayAllBooks(Logger logger){
        return  bookService.getAllBooks(logger);

    }

    public List<Book> displaySortedBooks(String type,Logger logger){
        BookSorting sorting= getBookType(type);
        return bookService.getSortedBooks(sorting,logger);


    }

    public List<Book> displayLongLiedBooks(String type, int numberOfMonth, Logger logger){
        LongLiedBookSorting sorting= getLongLiedBookType(type);
        return bookService.getLongLiedBooks(sorting, numberOfMonth, logger);


    }

    public String checkBook(String book, Logger logger){
        boolean checking = bookService.checkBook(book, logger);
        if (checking) {
            return "Книга в наличии";
        }else{
            return "Книга не в наличии";
        }
    }

    public String displayBookDescription(String bookName,Logger logger){
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
            case "7" -> LongLiedBookSorting.DATE_UP;

            case "6" -> LongLiedBookSorting.DATE_DOWN;
            case "5" -> LongLiedBookSorting.PRICE_UP;
            case "4" -> LongLiedBookSorting.PRICE_DOWN;
            default -> LongLiedBookSorting.NONE;
        };

    }

    String getForDisplayType(String type){
        if (type.equals("Lbooks") ){
            return "1. Дата (возрастание)\n2.Дата (убывание)\n3.Цена (по возрастанию)\n4.Цена (по убыванию) 5. Без фильтра";
        }
        return "1. По алфавиту (по возрастанию)\n2. По алфавиту (по убыванию)\n" +
                "3. В наличии\n4. Цена (по возрастанию)\n5. Цена (по убыванию)\n" +
                "6. Дата (по возрастанию)\n7. Дата (по убыванию)\n8.Без фильтра";
    }

    Optional<Book> getBookByTitle(String name, Logger logger){
        return bookService.getBookByTitle(name, logger);
    }

    public boolean receiveBook (String name,Logger logger){
        return bookService.receiveBook(name, logger);
    }

    public void setLastPurchase(List<Book> books){
        bookService.setLastPurchase(books);
    }

//    public String importBook(String fileName){
//        return bookService.importNewBook(fileName);
//    }

//    public String exportBook(String fileName){
//        return bookService.exportBook(fileName);
//    }

}

package com.example.application.controllers;

import com.example.application.dto.ReceiveRequest;
import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.types.OrderSorting;
import com.example.application.model.types.OrderStatus;

import com.example.application.services.BookService;
import com.example.application.services.BookShopFacade;
import com.example.application.services.SettingsService;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class BookStoreController {

    private BookService bookService;
    private BookShopFacade bookshop;
    private SettingsService settingsService;


    private static final Logger logger = LogManager.getLogger(BookStoreController.class.getName());

    @PatchMapping(value = "/receive", produces = "text/plain; charset=UTF-8" )
    public String receiveBook(@RequestBody ReceiveRequest book) {
        System.out.println("book " + book.getBookName());
        Boolean checking = bookService.receiveBook(book.getBookName(), logger);
        if (!checking) return "Ошибка";

        List<Order> orders = bookshop.getSortedOrders(
                getOrderSorting("6"), logger
        );

        List<Order> toChange = new ArrayList<>();
        List<Book> toChangeBooks = new ArrayList<>();
        for (Order o : orders) {

            if (o.checkUpdateByBook(book.getBookName()) == OrderStatus.DONE) {
                toChange.add(o);
                toChangeBooks.addAll(o.getBooks());
            }
        }

        if (!toChange.isEmpty()) {
            bookshop.updateOrders(toChange, logger);
            bookService.setLastPurchase(toChangeBooks, logger);
        }

        String warehouseFunction = settingsService.getWarehouseOption();
        if (warehouseFunction.equals("true")) {
            Book bookObject = bookService.getBookByTitle(book.getBookName(), logger);
            if (bookObject != null) {
                bookService.cancellRequestsByBook(bookObject, logger);
            }

        }

        logger.info("Обработка команды заквоза книги в отсеке запросов завершена");
        return "Книга завезена";


    }

    private OrderSorting getOrderSorting(String type) {
        return switch (type) {
            case "1" -> OrderSorting.PRICE_UP;
            case "2" -> OrderSorting.PRICE_DOWN;
            case "3" -> OrderSorting.DATE_UP;
            case "4" -> OrderSorting.DATE_DOWN;
            case "5" -> OrderSorting.DONE;
            case "6" -> OrderSorting.NEW;
            case "7" -> OrderSorting.CANCELLED;
            default -> OrderSorting.DATE_UP;
        };
    }


   public BookStoreController(BookService bookService, BookShopFacade bookshop, SettingsService settingsService) {
        this.bookService = bookService;
        this.bookshop = bookshop;
        this.settingsService = settingsService;
   }


}








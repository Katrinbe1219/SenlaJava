package task_3_4.controllers;

import task_3_4.model.Book;
import task_3_4.model.Order;
import task_3_4.model.types.BookSorting;
import task_3_4.model.types.LongLiedBookSorting;
import task_3_4.services.BookService;

import java.util.List;

public class BookController {
    private BookService bookService;

    BookController(BookService bookService) {
        this.bookService = bookService;
    }

    public List<Book> displayAllBooks(){
        return  bookService.getAllBooks();

    }

    public List<Book> displaySortedBooks(String type){
        BookSorting sorting= getBookType(type);
        return bookService.getSortedBooks(sorting);


    }

    public List<Book> displayLongLiedBooks(String type){
        LongLiedBookSorting sorting= getLongLiedBookType(type);
        return bookService.getLongLiedBooks(sorting);


    }

    public String checkBook(String book){
        boolean checking = bookService.checkBook(book);
        if (checking) {
            return "Книга в наличии";
        }else{
            return "Книга не в наличии";
        }
    }

    public String displayBookDescription(String bookName){
        String description = bookService.getBookDescription(bookName);
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

    String getForDisplayType(String type){
        if (type.equals("Lbooks") ){
            return "1. Дата (возрастание)\n2.Дата (убывание)\n3.Цена (по возрастанию)\n4.Цена (по убыванию) 5. Без фильтра";
        }
        return "1. По алфавиту (по возрастанию)\n2. По алфавиту (по убыванию)\n" +
                "3. В наличии\n4. Цена (по возрастанию)\n5. Цена (по убыванию)\n" +
                "6. Дата (по возрастанию)\n7. Дата (по убыванию)\n8.Без фильтра";
    }

    Book getBookByTitle(String name){
        return bookService.getBookByTitle(name);
    }

    public boolean receiveBook (String name){
        return bookService.receiveBook(name);
    }

    public void setLastPurchase(List<Book> books){
        bookService.setLastPurchase(books);
    }



}

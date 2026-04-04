package com.example.application.services;

import com.example.application.dto.AuthorDTO;
import com.example.application.dto.BookDTO;
import com.example.application.errors.CanNotMakeExecution;
import com.example.application.exceptions.BookCanBotBeCreated;
import com.example.application.hibernate.BookHibImpl;
import com.example.application.hibernate.RequestHibImpl;
import com.example.application.model.Author;
import com.example.application.model.Order;
import com.example.application.model.RequestResult;
import com.example.application.model.types.*;
import com.example.application.model.Book;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;


// service for warehouse instead of previous one
@Service
public class BookService {

    BookHibImpl bookHibImpl;

    RequestHibImpl requestHibImpl;

    public BookService(BookHibImpl bookHibImpl, RequestHibImpl requestHibImpl) {
        this.bookHibImpl = bookHibImpl;
        this.requestHibImpl = requestHibImpl;

    }


    public List<BookDTO> getAllBooks(Logger logger){
        try {
            return bookHibImpl.getSortedBooks("title",false, logger).stream().map(this::toBookDTO).toList();
        } catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }

    }


    public boolean receiveBook(String title,Logger logger){
        try {
            bookHibImpl.save(logger, title);
            return true;
        } catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return false;
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return false;
        }
    }

    // используется для изменения в бд, не выводит в ответе RestController, поэтому оставила Book
    public Book getBookByTitle(String title, Logger logger){
        try {
            return bookHibImpl.getBookByTitle(logger, title, null);
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }

    }

    public boolean checkBook (Integer title, Logger logger){
        try {
            Book book  = bookHibImpl.getBookById(logger, title);
            if (book == null) return false;
            return book.getStatus() == BookStatus.IN_STOCK;
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return false;
        }


    }

    public List<BookDTO> getSortedBooks(BookSorting sortingType, Logger logger){
        try {
            List<Book> sortedBooks;

            switch (sortingType) {
                case ALPHABETICAL_UP:
                    sortedBooks= bookHibImpl.getSortedBooks("title", false, logger);
                    break;

                case ALPHABETICAL_DOWN:
                    sortedBooks= bookHibImpl.getSortedBooks("title", true, logger);
                    break;

                case INSTOCK:
                    sortedBooks= bookHibImpl.getSortedBooks("status", "I", logger);
                    break;

                case DATE_UP:

                    sortedBooks= bookHibImpl.getSortedBooks("admissionDate", false, logger);
                    break;

                case DATE_DOWN:
                    sortedBooks= bookHibImpl.getSortedBooks("admissionDate", true, logger);
                    break;

                case PRICE_UP:
                    sortedBooks= bookHibImpl.getSortedBooks("price", false, logger);
                    break;

                case PRICE_DOWN:
                    sortedBooks= bookHibImpl.getSortedBooks("price", true, logger);
                    break;

                case ALL:
                    sortedBooks= bookHibImpl.findAll(logger);
                    break;

                default:
                    sortedBooks= bookHibImpl.findAll(logger);
                    break;
            }

            return sortedBooks.stream().map(this::toBookDTO).toList();
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }


    }

    public List<Book> getBooksByTitles(List<String> titles, Logger logger){
        try {
            return bookHibImpl.getBookByTitles(titles, logger);
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }

    }
    public String getBookDescription(Integer bookName,Logger logger){
        try {
            Book book  = bookHibImpl.getBookById(logger, bookName);
            if (book == null)  return "Такой книги не нашлось";
            return book.getDescription();
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return "Ошибка на сервере";
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return "Ошибка на сервере";
        }


    }

    public void setLastPurchase(List<Book> books, Logger logger){
        bookHibImpl.updateBooksLastPurchase(books, logger);
    }

    public List<BookDTO> getLongLiedBooks(LongLiedBookSorting sortingType, int numberOfMonth, Logger logger){
        try {
            List<Book> books;
            switch(sortingType) {
                case PRICE_DOWN -> books= bookHibImpl.getLongLiedBooks(numberOfMonth, "price", true, logger);

                case DATE_UP ->books = bookHibImpl.getLongLiedBooks(numberOfMonth, "lastPurchaseDate", true, logger);


                case DATE_DOWN -> books= bookHibImpl.getLongLiedBooks(numberOfMonth, "lastPurchaseDate", false, logger);



                case PRICE_UP -> books= bookHibImpl.getLongLiedBooks(numberOfMonth, "price", false, logger);


                case NONE -> books= bookHibImpl.getLongLiedBooks(numberOfMonth, "id", true, logger);
                default -> books= bookHibImpl.getLongLiedBooks(numberOfMonth, "id", true, logger);


            };

            return books.stream().map(this::toBookDTO).toList();
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }

    }

    Book getBookById(Integer id, Logger logger){
        try {
            List<Book> books = bookHibImpl.findAll(logger);
            for (Book book : books) {
                if (book.getId() == id){
                    return book;
                }
            }
            return null;

        } catch (CanNotMakeExecution e) {
            System.out.println("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        }catch (Exception e) {
            System.out.println("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }
    }


    public void cancellRequestsByBook(Book book, Logger logger){
        try {
            requestHibImpl.deleteManyByBook(book, logger);
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());

        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());

        }


    }




    public void cancellOrderRequests(Order order, Logger logger){
        try {
            requestHibImpl.deleteManyByOrder(order, logger);
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());

        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());

        }

    }



    public List<RequestResult> getSortedRequests(RequestSorting sortingType, Logger logger){

        try {
            List<RequestResult> requests = null;
            switch(sortingType){
                case RequestSorting.ALPHABETICAL_UP -> {
                    requests = requestHibImpl.getRequestsSorted("b.title", "ASC", logger);
                }
                case RequestSorting.ALPHABETICAL_DOWN -> {
                    requests = requestHibImpl.getRequestsSorted("b.title", "DESC", logger);
                }
                case RequestSorting.AMOUNT_UP -> {
                    requests = requestHibImpl.getRequestsSorted("amount", "ASC", logger);
                }
                case RequestSorting.AMOUNT_DOWN -> {
                    requests = requestHibImpl.getRequestsSorted("amount", "DESC", logger);
                }
            }


            return requests;
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }
    }

    private BookDTO toBookDTO(Book old){
        return new BookDTO(old.getStatus(),
                old.getTitle(),
                old.getYear(),
                old.getPrice(),
                old.getGenre(),
                toAuthorDTO(old.getAuthor()),
                old.getLastPurchaseDate(),
                old.getAdmissionDate());
    }

    private AuthorDTO toAuthorDTO(Author old){
        return new AuthorDTO(old.getName(), old.getSurname(), old.getPaternal());
    }







}

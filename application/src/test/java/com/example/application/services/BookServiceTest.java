package com.example.application.services;

import com.example.application.dto.BookDTO;
import com.example.application.errors.CanNotMakeExecution;
import com.example.application.hibernate.BookHibImpl;
import com.example.application.hibernate.RequestHibImpl;
import com.example.application.model.Author;
import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.RequestResult;
import com.example.application.model.types.*;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    BookHibImpl bookHibImpl;

    @Mock
    RequestHibImpl requestHibImpl;

    @InjectMocks
    BookService bookService;

    @Mock
    Logger logger;

    private List<Book> getListBooks(){
        Author basicAuthor = new Author("Name", "Surname", "Paternal", 123L);

        return List.of(
                new Book(1,"Book1", basicAuthor,
                        2006, BookStatus.OUT_OF_STOCK, 12D, BookTypes.FANTASY),
                new Book(1,"Book2", basicAuthor,
                        2006, BookStatus.IN_STOCK, 12D, BookTypes.CLASSICAL)
        );
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getAllBooksPositive")
    void getAllBooksPositive(){
        when(bookHibImpl.getSortedBooks(anyString(), anyBoolean(), eq(logger)))
                .thenReturn(
                  getListBooks()
        );

        List<BookDTO> books = bookService.getAllBooks(logger);
        Assertions.assertNotNull(books);
        Assertions.assertFalse(books.isEmpty());
    }


    @Test
    @Tag("negative_tests")
    @DisplayName("getAllBooksNegative")
    void getAllBooksNegative(){
        when(bookHibImpl.getSortedBooks(anyString(), anyBoolean(), eq(logger)))
                .thenThrow(new CanNotMakeExecution("pr"));

        Assertions.assertNull(bookService.getAllBooks(logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("receiveBookPositive")
    void receiveBookPositive(){
        when(bookHibImpl.save(eq(logger), anyString())).thenReturn(null);
        Assertions.assertTrue(bookService.receiveBook("title", logger));

    }

    @Test
    @Tag("negative_tests")
    @DisplayName("receiveBookNegative")
    void receiveBookNegative(){
        doThrow(CanNotMakeExecution.class).when(bookHibImpl).save(eq(logger), anyString());
        Assertions.assertFalse(bookService.receiveBook("title", logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getBookByTitlePositive")
    void getBookByTitlePositive(){
        when(bookHibImpl.getBookByTitle(eq(logger), anyString(),eq(null)))
                .thenReturn(new Book())
                .thenReturn(null);

        Assertions.assertNotNull(bookService.getBookByTitle("title", logger));
        Assertions.assertNull(bookService.getBookByTitle("nullEч", logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("getBookByTitleNegative")
    void getBookByTitleNegative(){
        when(bookHibImpl.getBookByTitle(eq(logger), anyString(), eq(null)))
                .thenThrow(CanNotMakeExecution.class);
        Assertions.assertNull(bookService.getBookByTitle("negTest", logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("checkBookPositive")
    void checkBookPositive(){
        Book experiment = new Book();
        experiment.setStatus(BookStatus.IN_STOCK);

        when(bookHibImpl.getBookById(eq(logger), anyInt()))
                .thenReturn(experiment)
                .thenReturn(null);

        Assertions.assertTrue(bookService.checkBook(12, logger));
        Assertions.assertFalse(bookService.checkBook(13, logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("checkBookNegative")
    void checkBookNegative(){

        when(bookHibImpl.getBookById(eq(logger), anyInt()))
                .thenThrow(CanNotMakeExecution.class);
        Assertions.assertFalse(bookService.checkBook(13, logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("checkBookPositive")
    void getSortedBooksPositive(){
        when(bookHibImpl.getSortedBooks(anyString(), anyBoolean(), eq(logger))).thenReturn(
                getListBooks()
        );

        Assertions.assertNotNull(bookService.getSortedBooks(BookSorting.ALPHABETICAL_UP, logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("checkBookNegative")
    void getSortedBooksNegative(){
        when(bookHibImpl.getSortedBooks(anyString(), anyBoolean(), eq(logger))).thenThrow(CanNotMakeExecution.class);

        Assertions.assertNull(bookService.getSortedBooks(BookSorting.ALPHABETICAL_UP, logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("checkBookPositive")
    void getBooksByTitlesPositive(){
        List<Book> check = getListBooks();
        when(bookHibImpl.getBookByTitles(anyList(),  eq(logger))).thenReturn(
                check
        );
        List<Book> books = bookService.getBooksByTitles(List.of("Titles"), logger);


        Assertions.assertNotNull(books);
        //check
        Assertions.assertEquals(check, books);
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("checkBookNegative")
    void getBooksByTitlesNegative(){
        when(bookHibImpl.getBookByTitles(anyList(),  eq(logger))).thenThrow(CanNotMakeExecution.class);
        Assertions.assertNull(bookService.getBooksByTitles(List.of("Titles"), logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getDescriptionPositive")
    void getDescriptionPositive(){
        when(bookHibImpl.getBookById(eq(logger), anyInt())).thenReturn(null);
        Assertions.assertEquals("Такой книги не нашлось",bookService.getBookDescription( 1,logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("getDescriptionNegative")
    void getDescriptionNegative(){
        when(bookHibImpl.getBookById(eq(logger), anyInt())).thenThrow(CanNotMakeExecution.class);
        Assertions.assertEquals("Ошибка на сервере",bookService.getBookDescription( 1,logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getLongLiedBooksPositive")
    void getLongLiedBooksPositive(){
        when(bookHibImpl.getLongLiedBooks(anyInt(), anyString(), anyBoolean(), eq(logger))).thenReturn(getListBooks());
        Assertions.assertNotNull(bookService.getLongLiedBooks(LongLiedBookSorting.PRICE_DOWN, 1,logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("getLongLiedBooksNegative")
    void getLongLiedBooksNegative(){
        when(bookHibImpl.getLongLiedBooks(anyInt(), anyString(), anyBoolean(), eq(logger)))
                .thenThrow(new CanNotMakeExecution("тест"));
        Assertions.assertNull(bookService.getLongLiedBooks(LongLiedBookSorting.PRICE_DOWN, 1,logger));
        verify(logger).error(contains("тест"));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("cancellOrderRequestPositive")
    void cancellOrderRequestPositive(){

        doNothing().when(requestHibImpl).deleteManyByOrder(any(Order.class), eq(logger));
        bookService.cancellOrderRequests(new Order(), logger);
        verify(requestHibImpl).deleteManyByOrder(any(Order.class), eq(logger));
        verify(logger, never()).error(anyString());
    }


    @Test
    @Tag("negative_tests")
    @DisplayName("cancellOrderRequestNegative")
    void cancellOrderRequestNegative(){
        doThrow(new CanNotMakeExecution("тест")).when(requestHibImpl).deleteManyByOrder(any(Order.class), eq(logger));
        assertDoesNotThrow(() -> bookService.cancellOrderRequests(new Order(), logger));

        verify(logger).error(contains("Проблема CanNotMakeExecution: "));
        verify(logger).error(contains("тест"));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("cancellRequestByBookPositive")
    void cancellRequestByBookPositive(){
        doNothing().when(requestHibImpl).deleteManyByBook(any(Book.class), eq(logger));
        bookService.cancellRequestsByBook(new Book(), logger);
        verify(requestHibImpl).deleteManyByBook(any(Book.class), eq(logger));
        verify(logger, never()).error(anyString());
    }


    @Test
    @Tag("negative_tests")
    @DisplayName("cancellRequestByBookNegative")
    void cancellRequestByBookNegative(){
        doThrow(new CanNotMakeExecution("тест")).when(requestHibImpl).deleteManyByBook(any(Book.class), eq(logger));
        // сам метод не должен ничего бросать, он имеет try-catch
        assertDoesNotThrow(() -> bookService.cancellRequestsByBook(new Book(), logger));

        verify(logger).error(contains("Проблема CanNotMakeExecution: "));
        verify(logger).error(contains("тест"));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getSortedRequestsPositive")
    void getSortedRequestsPositive(){
        when(requestHibImpl.getRequestsSorted(anyString(), anyString(), eq(logger)))
                .thenReturn(
                        List.of(
                                new RequestResult("i", 1L)
                        )
                )
                .thenReturn(null);
        Assertions.assertNotNull(bookService.getSortedRequests(RequestSorting.ALPHABETICAL_UP, logger));
        Assertions.assertNull(bookService.getSortedRequests(RequestSorting.ALPHABETICAL_UP, logger));
        verify(logger, never()).error(anyString());

    }


    @Test
    @Tag("negative_tests")
    @DisplayName("getSortedRequestsNegative")
    void getSortedRequestsNegative(){
        when(requestHibImpl.getRequestsSorted(anyString(), anyString(), eq(logger)))
                .thenThrow(new CanNotMakeExecution("тест"));
        assertDoesNotThrow(() ->bookService.getSortedRequests(RequestSorting.ALPHABETICAL_UP, logger));
        verify(logger).error(contains("Проблема CanNotMakeExecution: "));
        verify(logger).error(contains("тест"));
    }

}

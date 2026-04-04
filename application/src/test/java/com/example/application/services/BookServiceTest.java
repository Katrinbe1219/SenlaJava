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
    @DisplayName("getAllBooksIfBooksWereFetched")
    void getAllBooksIfBooksWereFetched(){
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
    @DisplayName("getAllBooksIfFetchingFailed")
    void getAllBooksIfFetchingFailed(){
        when(bookHibImpl.getSortedBooks(anyString(), anyBoolean(), eq(logger)))
                .thenThrow(new CanNotMakeExecution("pr"));

        Assertions.assertNull(bookService.getAllBooks(logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("receiveBookIfBookWasReceived")
    void receiveBookIfBookWasReceived(){
        when(bookHibImpl.save(eq(logger), anyString())).thenReturn(null);
        Assertions.assertTrue(bookService.receiveBook("title", logger));

    }

    @Test
    @Tag("negative_tests")
    @DisplayName("receiveBookIfSavingFailed")
    void receiveBookIfSavingFailed(){
        doThrow(CanNotMakeExecution.class).when(bookHibImpl).save(eq(logger), anyString());
        Assertions.assertFalse(bookService.receiveBook("title", logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getBookByTitleIfBookWasFetched")
    void getBookByTitleIfBookWasFetched(){
        when(bookHibImpl.getBookByTitle(eq(logger), anyString(),eq(null)))
                .thenReturn(new Book())
                .thenReturn(null);

        Assertions.assertNotNull(bookService.getBookByTitle("title", logger));
        Assertions.assertNull(bookService.getBookByTitle("nullEч", logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("getBookByTitleIfFetchingFailed")
    void getBookByTitleIfFetchingFailed(){
        when(bookHibImpl.getBookByTitle(eq(logger), anyString(), eq(null)))
                .thenThrow(CanNotMakeExecution.class);
        Assertions.assertNull(bookService.getBookByTitle("negTest", logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("checkBookIfFetchingSucceeded")
    void checkBookIfFetchingSucceeded(){
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
    @DisplayName("checkBookIfFetchingFailed")
    void checkBookIfFetchingFailed(){

        when(bookHibImpl.getBookById(eq(logger), anyInt()))
                .thenThrow(CanNotMakeExecution.class);
        Assertions.assertFalse(bookService.checkBook(13, logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getSortedBooksIfSortingSucceeded")
    void getSortedBooksIfSortingSucceeded(){
        when(bookHibImpl.getSortedBooks(anyString(), anyBoolean(), eq(logger))).thenReturn(
                getListBooks()
        );

        Assertions.assertNotNull(bookService.getSortedBooks(BookSorting.ALPHABETICAL_UP, logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("getSortedBooksIfSortingFailed")
    void getSortedBooksIfSortingFailed(){
        when(bookHibImpl.getSortedBooks(anyString(), anyBoolean(), eq(logger))).thenThrow(CanNotMakeExecution.class);

        Assertions.assertNull(bookService.getSortedBooks(BookSorting.ALPHABETICAL_UP, logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getBooksByTitlesIfFetchingSucceeded")
    void getBooksByTitlesIfFetchingSucceeded(){
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
    @DisplayName("getBooksByTitlesIfFetchingFailed")
    void getBooksByTitlesIfFetchingFailed(){
        when(bookHibImpl.getBookByTitles(anyList(),  eq(logger))).thenThrow(CanNotMakeExecution.class);
        Assertions.assertNull(bookService.getBooksByTitles(List.of("Titles"), logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getDescriptionIfFetchingSucceeded")
    void getDescriptionIfFetchingSucceeded(){
        when(bookHibImpl.getBookById(eq(logger), anyInt())).thenReturn(null);
        Assertions.assertEquals("Такой книги не нашлось",bookService.getBookDescription( 1,logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("getDescriptionIfFetchingFailed")
    void getDescriptionIfFetchingFailed(){
        when(bookHibImpl.getBookById(eq(logger), anyInt())).thenThrow(CanNotMakeExecution.class);
        Assertions.assertEquals("Ошибка на сервере",bookService.getBookDescription( 1,logger));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getLongLiedBooksIfFetchingSucceeded")
    void getLongLiedBooksIfFetchingSucceeded(){
        when(bookHibImpl.getLongLiedBooks(anyInt(), anyString(), anyBoolean(), eq(logger))).thenReturn(getListBooks());
        Assertions.assertNotNull(bookService.getLongLiedBooks(LongLiedBookSorting.PRICE_DOWN, 1,logger));
    }

    @Test
    @Tag("negative_tests")
    @DisplayName("getLongLiedBooksIfFetchingFailed")
    void getLongLiedBooksIfFetchingFailed(){
        when(bookHibImpl.getLongLiedBooks(anyInt(), anyString(), anyBoolean(), eq(logger)))
                .thenThrow(new CanNotMakeExecution("тест"));
        Assertions.assertNull(bookService.getLongLiedBooks(LongLiedBookSorting.PRICE_DOWN, 1,logger));
        verify(logger).error(contains("тест"));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("cancellOrderRequestIfDeletingSucceeded")
    void cancellOrderRequestIfDeletingSucceeded(){

        doNothing().when(requestHibImpl).deleteManyByOrder(any(Order.class), eq(logger));
        bookService.cancellOrderRequests(new Order(), logger);
        verify(requestHibImpl).deleteManyByOrder(any(Order.class), eq(logger));
        verify(logger, never()).error(anyString());
    }


    @Test
    @Tag("negative_tests")
    @DisplayName("cancellOrderRequestIfDeletingFailed")
    void cancellOrderRequestIfDeletingFailed(){
        doThrow(new CanNotMakeExecution("тест")).when(requestHibImpl).deleteManyByOrder(any(Order.class), eq(logger));
        assertDoesNotThrow(() -> bookService.cancellOrderRequests(new Order(), logger));

        verify(logger).error(contains("Проблема CanNotMakeExecution: "));
        verify(logger).error(contains("тест"));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("cancellRequestByBookIfDeletingSucceeded")
    void cancellRequestByBookIfDeletingSucceeded(){
        doNothing().when(requestHibImpl).deleteManyByBook(any(Book.class), eq(logger));
        bookService.cancellRequestsByBook(new Book(), logger);
        verify(requestHibImpl).deleteManyByBook(any(Book.class), eq(logger));
        verify(logger, never()).error(anyString());
    }


    @Test
    @Tag("negative_tests")
    @DisplayName("cancellRequestByBookIfDeletingFailed")
    void cancellRequestByBookIfDeletingFailed(){
        doThrow(new CanNotMakeExecution("тест")).when(requestHibImpl).deleteManyByBook(any(Book.class), eq(logger));
        // сам метод не должен ничего бросать, он имеет try-catch
        assertDoesNotThrow(() -> bookService.cancellRequestsByBook(new Book(), logger));

        verify(logger).error(contains("Проблема CanNotMakeExecution: "));
        verify(logger).error(contains("тест"));
    }

    @Test
    @Tag("positive_tests")
    @DisplayName("getSortedRequestsIfFetchingSucceeded")
    void getSortedRequestsIfFetchingSucceeded(){
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
    @DisplayName("getSortedRequestsIfFetchingFailed")
    void getSortedRequestsIfFetchingFailed(){
        when(requestHibImpl.getRequestsSorted(anyString(), anyString(), eq(logger)))
                .thenThrow(new CanNotMakeExecution("тест"));
        assertDoesNotThrow(() ->bookService.getSortedRequests(RequestSorting.ALPHABETICAL_UP, logger));
        verify(logger).error(contains("Проблема CanNotMakeExecution: "));
        verify(logger).error(contains("тест"));
    }

}

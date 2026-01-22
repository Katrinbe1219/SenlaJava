package com.example.application.services;

import com.example.application.dao.BookImplementation;
import com.example.application.dao.RequestImplementation;
import com.example.application.errors.CanNotMakeExecution;
import com.example.application.exceptions.BookCanBotBeCreated;
import com.example.application.hibernate.BookHibImpl;
import com.example.application.hibernate.RequestHibImpl;
import com.example.application.model.Order;
import com.example.application.model.Request;
import com.example.application.model.RequestResult;
import com.example.application.model.types.*;
import com.example.application.model.Book;

import org.apache.logging.log4j.Logger;
import com.example.custom_applications.Inject;

import java.sql.Connection;
import java.util.*;


// service for warehouse instead of previous one
@Inject
public class BookService {

    @Inject
    BookHibImpl bookHibImpl;

    @Inject
    RequestHibImpl requestHibImpl;


    public List<Book> getAllBooks(Logger logger){
        try {
            return bookHibImpl.findAll(logger);
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

    public boolean checkBook (String title, Logger logger){
        try {
            Book book  = bookHibImpl.getBookByTitle(logger, title, null);
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

    public List<Book> getSortedBooks(BookSorting sortingType, Logger logger){
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

            return sortedBooks;
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }


    }

    public String getBookDescription(String bookName,Logger logger){
        try {
            Book book  = bookHibImpl.getBookByTitle(logger, bookName, null);
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

    public List<Book> getLongLiedBooks(LongLiedBookSorting sortingType, int numberOfMonth, Logger logger){
        try {
            return switch(sortingType) {
                case PRICE_DOWN -> bookHibImpl.getLongLiedBooks(numberOfMonth, "price", true, logger);

                case DATE_UP -> bookHibImpl.getLongLiedBooks(numberOfMonth, "lastPurchaseDate", true, logger);


                case DATE_DOWN ->  bookHibImpl.getLongLiedBooks(numberOfMonth, "lastPurchaseDate", false, logger);



                case PRICE_UP ->  bookHibImpl.getLongLiedBooks(numberOfMonth, "price", false, logger);


                case NONE ->  bookHibImpl.getLongLiedBooks(numberOfMonth, "id", true, logger);


            };
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



    //---------------------------------------------------------------------------------------

//    public String importNewBook(String filename){
//
//
//        try(BufferedReader br = new BufferedReader(
//                    new InputStreamReader(
//                            new FileInputStream(filename), StandardCharsets.UTF_8)
//                )){
//            String line;
//            br.readLine(); // пропускаем заголовок
//
//            while((line = br.readLine()) != null){
//                String[] fields = line.split(";");
//                Book checking = getBookById(parseInt(fields[0]));
//                Book newBook = new Book(
//                        parseInt(fields[0]), // id
//                        fields[1], // title
//                        fields[2], // author
//                        parseInt(fields[3]), // year
//                        parseBookStatus(fields[4]), // status
//                        parseDouble(fields[5]), // price
//                        parseBookType(fields[6])
//                );
//                if (checking == null) {
//                    bookRepository.addNewBook(newBook);
//                    bookRepository.checkMaxBookId(parseInt(fields[0]));
//                }else {
//                    checkOrChangeBook(checking, newBook);
//                }
//
//
//            }
//
//        }
//        catch (IOException | BookCanBotBeCreated | ArrayIndexOutOfBoundsException e){
//            return e.getMessage();
//        }
//        return "";
//    }
//

//    public String exportBook(String title){
//
//        Book book = getBookByTitle(title);
//        if (book == null) return "Книга не была найдена";
//
//        String filename = title + ".csv";
//        try (BufferedWriter bw = new BufferedWriter(
//                new OutputStreamWriter(
//                        new FileOutputStream(filename),  StandardCharsets.UTF_8
//                )
//        )){
//
//
//            String[] headers = {"ID", "Title", "Author", "Year", "Status", "Price", "Type"};
//            bw.write(String.join(";", headers));
//            bw.newLine();
//            bw.write(getCsvBookLine(book));
//
//
//
//        } catch (IOException e) {
//           return e.getMessage();
//        }
//
//        return "";
//
//    }

    private Integer parseInt(String value) throws BookCanBotBeCreated{

        try {
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            throw new BookCanBotBeCreated(e.getMessage());
        }
    }

    private BookStatus parseBookStatus(String value) throws BookCanBotBeCreated{
        if (value.equalsIgnoreCase("in-stock")){
            return BookStatus.IN_STOCK;
        }

        if (value.equalsIgnoreCase("out-of-stock")){
            return BookStatus.OUT_OF_STOCK;
        }

        throw new BookCanBotBeCreated("Неправильно указан статус книги");
    }

    private Double parseDouble(String value) throws  BookCanBotBeCreated{

        try {
            return Double.parseDouble(value);
        }catch (NumberFormatException e){
            throw new BookCanBotBeCreated(e.getMessage());
        }
    }

    private BookTypes parseBookType(String value) throws BookCanBotBeCreated{

        if (value.equalsIgnoreCase("fantasy")) return BookTypes.FANTASY;
        if (value.equalsIgnoreCase("classical")) return BookTypes.CLASSICAL;
        if (value.equalsIgnoreCase("history")) return BookTypes.HISTORY;


        throw new BookCanBotBeCreated("Неправильно указан вид книги");
    }

//    private void checkOrChangeBook(Book old_, Book new_){
//        boolean checking = old_.compareTo(new_)  == 0;
//        if (checking) return;
//
//        if (!old_.getTitle().equals( new_.getTitle())){
//                old_.setTitle(new_.getTitle());
//        }
//        if (old_.getStatus() != new_.getStatus()){
//            old_.setStatus(new_.getStatus());
//        }
//
//        if (old_.getLastPurchaseDate() != new_.getLastPurchaseDate()){
//            old_.setLastPurchaseDate(new_.getLastPurchaseDate());
//        }
//
//        if (old_.getAdmissionDate() != new_.getAdmissionDate()){
//            old_.setAdmissionDate(new_.getAdmissionDate());
//        }
//
//        if (old_.getPrice() != new_.getPrice()){
//            old_.setPrice(new_.getPrice());
//        }
//
//        if (old_.getYear() != new_.getYear()){
//            old_.setYear(new_.getYear());
//        }
//
//        if (old_.getGenre() != new_.getGenre()){
//            old_.setGenre(new_.getGenre());
//        }
//
//        if (!old_.getAuthor().equals(new_.getAuthor())){
//            old_.setAuthor(new_.getAuthor());
//        }
//    }

    private  String getCsvBookLine(Book book){
        return String.valueOf(book.getId()) +  ';' +
                book.getTitle() + ';' + book.getAuthor() + ';'
                + book.getYear() + ';' +  getStringBookStatus(book.getStatus()) + ';' +
                book.getPrice() + ';' + getStringBookType(book.getGenre());

    }

    private String getStringBookStatus(BookStatus status){
        return switch (status){
            case BookStatus.IN_STOCK -> "in-stock";
            case BookStatus.OUT_OF_STOCK -> "out-of-stock";
        };
    }

    private String getStringBookType(BookTypes type){
        return switch (type){
            case FANTASY -> "fantasy";
            case CLASSICAL -> "classical";
            case HISTORY -> "history";
        };
    }

//    public String importRequest(String filename){
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(
//                        new FileInputStream(filename), StandardCharsets.UTF_8
//                )
//        )){
//            String line;
//            br.readLine();
//
//            while ((line = br.readLine()) != null) {
//                String[] fields = line.split(";");
//                Request checking = getRequestById(parseInt(fields[0]));
//
//                Book book = getBookById(parseInt(fields[1]));
//                if (book == null) return "Для данной книги не может быть создан запрос";
//
//                Order order = getOrderById(parseInt(fields[2]));
//                if (order == null) return "Данный запрос не прикреплен к заказу";
//                if (!checkConnectionRequestOrder(parseInt(fields[1]), order)) return "Заказ не имеет такого наполнения";
//
//                Request newRequest = new Request(parseInt(fields[0]),book, order);
//                if (checking == null) {
//                    requestRepository.add(newRequest);
//                    requestRepository.checkMaxRequestId(parseInt(fields[0]));
//                }else {
//                    checkOrChangeRequest(checking, newRequest);
//                }
//
//            }
//        }
//        catch (IOException e){
//            return e.getMessage();
//        }
//
//        return "";
//
//    }

//    public String exportRequest(String id_){
//        int id = parseInt(id_);
//        Request request = getRequestById(id);
//        if (request == null) return "Такой запрос не был найден";
//
//        String filename = "request_" + request.getId() + ".csv";
//        try (BufferedWriter bw = new BufferedWriter(
//                new OutputStreamWriter(
//                        new FileOutputStream(filename), StandardCharsets.UTF_8
//                )
//        )) {
//
//            String[] headers = {"ID", "Book", "Order"};
//            bw.write(String.join(";", headers));
//            bw.newLine();
//            bw.write(getCsvRequest(request));
//
//        }
//        catch (IOException e) {
//            return e.getMessage();
//        }
//
//        return "";
//    }
//
//    private String getCsvRequest(Request request){
//        return request.getId() + ";" + request.getBook().getId() + ";" + request.getOrder().getId();
//    }
//
//
//    private void checkOrChangeRequest(Request old_, Request new_){
//        boolean checking = old_.compareTo(new_) == 0;
//        if (checking) return;
//
//        if (old_.getBook().getId() != new_.getBook().getId()){
//            old_.setBook(new_.getBook());
//        }
//
//        if (old_.getOrder().getId() != new_.getOrder().getId()){
//            old_.setOrder(new_.getOrder());
//        }
//    }

    // предполагается, что запрос опирается на сущетсвующий заказ, который включает в себя книгу
    private boolean checkConnectionRequestOrder(int bookId, Order order){
        for (Book book: order.getBooks()){
            if (book.getId() == bookId) return true;
        }
        return  false;

    }



}

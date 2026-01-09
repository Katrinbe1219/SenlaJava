package com.example.application.services;

import com.example.application.dao.BookImplementation;
import com.example.application.dao.RequestImplementation;
import com.example.application.errors.CanNotMakeExecution;
import com.example.application.exceptions.BookCanBotBeCreated;
import com.example.application.model.Order;
import com.example.application.model.Request;
import com.example.application.model.RequestResult;
import com.example.application.model.types.*;
import com.example.application.model.Book;

import org.apache.logging.log4j.Logger;
import com.example.custom_applications.Inject;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


// service for warehouse instead of previous one
@Inject
public class BookService {

//    @Inject
//    private RequestRepository requestRepository;
    @Inject
    private RequestImplementation requestDao;

    @Inject
    private BookImplementation bookDao;

    @Inject
    Connection connection;

//    @Inject
//    OrderImplementation orderDao;


    public Optional<List<Book>> getAllBooks(Logger logger){
        try {
            return bookDao.findAll();
        } catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return Optional.empty();
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return Optional.empty();
        }

    }

    public boolean receiveBook(String title,Logger logger){
        try {
            bookDao.save(title);
            return true;
        } catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return false;
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return false;
        }
    }

    public Optional<Book> getBookByTitle(String title, Logger logger){
        try {
            return bookDao.getByTitle(title);
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return Optional.empty();
        }

    }

    public boolean checkBook (String book, Logger logger){
        try {
            Optional<Book> book_  = bookDao.getByTitle(book);
            if (book_.isEmpty()) return false;
            return book_.get().getStatus() == BookStatus.IN_STOCK;
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
            Optional<List<Book> >books_ = bookDao.findAll();
            if (books_.isEmpty()) { return null; }

            List<Book> books = books_.get();
            switch (sortingType) {
                case ALPHABETICAL_UP:
                    sortedBooks = books.stream()
                            .sorted(Comparator.comparing(Book::getTitle))
                            .toList();
                    break;
                case ALPHABETICAL_DOWN:
                    sortedBooks =  books.stream()
                            .sorted(Comparator.comparing(Book::getTitle).reversed())
                            .toList();
                    break;
                case INSTOCK:
                    sortedBooks = books.stream()
                            .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                            .toList();
                    break;

                case DATE_UP:
                    sortedBooks = books.stream()
                            .sorted(Comparator.comparing(Book::getYear))
                            .toList();
                    break;

                case DATE_DOWN:
                    sortedBooks = books.stream()
                            .sorted(Comparator.comparing(Book::getYear).reversed())
                            .toList();
                    break;

                case PRICE_UP:
                    sortedBooks = books.stream()
                            .sorted(Comparator.comparing(Book::getPrice))
                            .toList();
                    break;

                case PRICE_DOWN:
                    sortedBooks = books.stream()
                            .sorted(Comparator.comparing(Book::getPrice).reversed())
                            .toList() ;
                    break;

                case ALL:
                    sortedBooks = books.stream().toList();
                    break;

                default:
                    sortedBooks = books.stream().toList();
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
            Optional<Book> book_  = bookDao.getByTitle(bookName);
            if (book_.isEmpty())  return "Такой книги не нашлось";
            return book_.get().getDescription();
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return "Ошибка на сервере";
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return "Ошибка на сервере";
        }


    }

    public void setLastPurchase(List<Book> books){
        for (Book book : books) {
            book.setLastPurchaseDate(LocalDate.now());
        }
    }

    public List<Book> getLongLiedBooks(LongLiedBookSorting sortingType, int numberOfMonth, Logger logger){
        try {
            Optional<List<Book>> books_ = bookDao.findAll();
            if (books_.isEmpty()) { return null; }

            List<Book> books = books_.get();
            return switch(sortingType) {
                case PRICE_DOWN -> books.stream()
                        .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                        .filter(p ->{
                            LocalDate lastP = p.getLastPurchaseDate();
                            return lastP != null &&
                                    ChronoUnit.MONTHS.between(lastP, LocalDate.now()) > numberOfMonth;
                        })
                        .sorted(Comparator.comparing(Book::getPrice))
                        .toList();


                case DATE_UP -> books.stream()
                        .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                        .filter(p ->{
                            LocalDate lastP = p.getLastPurchaseDate();
                            return lastP != null &&
                                    ChronoUnit.MONTHS.between(lastP, LocalDate.now()) > numberOfMonth;
                        })
                        .sorted(Comparator.comparing(Book::getAdmissionDate))
                        .toList();

                case DATE_DOWN -> books.stream()
                        .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                        .filter(p ->{
                            LocalDate lastP = p.getLastPurchaseDate();
                            return lastP != null &&
                                    ChronoUnit.MONTHS.between(lastP, LocalDate.now()) > numberOfMonth;
                        })
                        .sorted(Comparator.comparing(Book::getAdmissionDate).reversed()).toList();

                case PRICE_UP -> books.stream()
                        .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                        .filter(p ->{
                            LocalDate lastP = p.getLastPurchaseDate();
                            return lastP != null &&
                                    ChronoUnit.MONTHS.between(lastP, LocalDate.now()) > numberOfMonth;
                        })
                        .sorted(Comparator.comparing(Book::getPrice).reversed())
                        .toList();

                case NONE -> books.stream()
                        .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                        .filter(p ->{
                            LocalDate lastP = p.getLastPurchaseDate();
                            return lastP != null &&
                                    ChronoUnit.MONTHS.between(lastP, LocalDate.now()) > numberOfMonth;
                        })
                        .toList();
            };
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }

    }

    Book getBookById(Integer id){
        try {
            Optional<List<Book>> books = bookDao.findAll();
            for (Book book : books.get()) {
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


    public void cancellRequestsByBook(Integer book_id, Logger logger){
        try {
            requestDao.deleteManyByBook(book_id);
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());

        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());

        }


    }




    public void cancellOrderRequests(Order order, Logger logger){
        try {
            requestDao.deleteManyByOrder(order.getId());
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());

        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());

        }

    }

    private Map<String, Integer> getRequestsGroupByBooks(Logger logger){
        try {
            Map<String, Integer> groupedRequests = new HashMap<>();
            List<RequestResult> requests = requestDao.getRequestsByBook();
            for (RequestResult request : requests) {
                if ( !groupedRequests.containsKey(request.getBook())){
                    groupedRequests.put(request.getBook(), 1);
                }else{
                    groupedRequests.merge(request.getBook(), 1, Integer::sum);
                }
            }

            return groupedRequests;
        }catch (CanNotMakeExecution e) {
            logger.error("Проблема CanNotMakeExecution: " + e.getMessage());
            return null;
        }catch (Exception e) {
            logger.error("Проблема не SQLExecution: " + e.getMessage());
            return null;
        }

    }

    public List<List<Object>> getSortedRequests(RequestSorting sortingType, Logger logger){

        Map<String, Integer> requestsGroupByBooks = getRequestsGroupByBooks(logger);
        List<List<Object>> listOfRequests = new ArrayList<>();

        for (String key : requestsGroupByBooks.keySet()) {
            listOfRequests.add(new ArrayList<>(Arrays.asList(key, requestsGroupByBooks.get(key))));
        }



        if (sortingType == RequestSorting.ALPHABETICAL_UP){
            sortRequestsByParameter(listOfRequests, 0, true);
        }else if (sortingType == RequestSorting.ALPHABETICAL_DOWN) {
            sortRequestsByParameter(listOfRequests, 0, false);
        } else if (sortingType == RequestSorting.AMOUNT_UP){
            sortRequestsByParameter(listOfRequests, 1, true);
        }else{
            sortRequestsByParameter(listOfRequests, 1, false);
        }

        return listOfRequests;
    }

    private void sortRequestsByParameter(List<List<Object>> groupedRequests, int index, boolean asc){
        Comparator<List<Object>> comparator = Comparator.comparing(
                list -> (Comparable)list.get(index)
        );
        if (!asc){
            comparator = comparator.reversed();
        }

        groupedRequests.sort(comparator);
    }


    private Request getRequestById(int id){
        Optional<List<Request>> req_ = requestDao.findAll();
        if (req_.isEmpty()) {return null;}

        List<Request> requests = req_.get();
        for (Request request: requests){
            if (request.getId() == id){
                return request;
            }
        }

        return null;
    }

//    private Order getOrderById(int id){
//        List<Order> orders = orderDao.getOrders();
//        for (Order order: orders){
//            if (order.getId() == id){
//                return order;
//            }
//        }
//        return null;
//    }



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

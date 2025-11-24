package task_3_4.services;

import task_3_4.exceptions.BookCanBotBeCreated;
import task_3_4.model.Order;
import task_3_4.model.Request;
import task_3_4.model.types.*;
import task_3_4.repositories.OrderRepository;
import task_3_4.repositories.RequestRepository;
import task_3_4.model.Book;
import task_3_4.repositories.BookRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// service for warehouse instead of previous one
public class BookService {
    private BookRepository bookRepository;
    private RequestRepository requestRepository;
    private OrderRepository orderRepository;

    public BookService(BookRepository bookRepository, RequestRepository requestRepository, OrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.requestRepository = requestRepository;
        this.orderRepository = orderRepository;
    }
    public List<Book> getAllBooks(){
        return bookRepository.getBooks();
    }

    public boolean receiveBook(String title){
        Book book_  = bookRepository.getBookByTitle(title);


        if (book_ == null) {
            System.out.println("Book was not found");
            return false;

        }
        book_.setStatus(BookStatus.IN_STOCK);
        book_.setAdmissionDate(LocalDate.now());
        cancellRequestsByBook(book_);
        return true;
    }

    public Book getBookByTitle(String title){
        return bookRepository.getBookByTitle(title);
    }

    public void cancellRequestsByBook(Book book){
        List<Request> requests = requestRepository.getRequests().stream()
                .filter(r -> r.getBook().equals(book)).toList();

        for (Request request : requests) {
            requestRepository.deleteRequest(request);
        }


    }

    public boolean checkBook (String book){
        Book book_  = bookRepository.getBookByTitle(book);
        if (book_ == null) return false;
        return book_.getStatus() == BookStatus.IN_STOCK;

    }

    public List<Book> getSortedBooks(BookSorting sortingType){
        List<Book> sortedBooks;
        List<Book> books = bookRepository.getBooks();
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

    }

    public String getBookDescription(String bookName){
        Book book = bookRepository.getBookByTitle(bookName);
        if (book == null) return "Такой книги не нашлось";
        return book.getDescription();

    }

    public void setLastPurchase(List<Book> books){
        for (Book book : books) {
            book.setLastPurchaseDate(LocalDate.now());
        }
    }


    public void cancellOrderRequests(Order order){
        List<Request> requests = requestRepository.getRequests().stream()
                .filter(r -> r.getOrder().equals(order)).toList();
        for (Request request : requests) {
            requestRepository.deleteRequest(request);
        }
    }

    private Map<String, Integer> getRequestsGroupByBooks(){
        Map<String, Integer> groupedRequests = new HashMap<>();
        List<Request> requests = requestRepository.getRequests();
        for (Request request : requests) {
            if ( !groupedRequests.containsKey(request.getBook().getTitle())){
                groupedRequests.put(request.getBook().getTitle(), 1);
            }else{
                groupedRequests.merge(request.getBook().getTitle(), 1, Integer::sum);
            }
        }

        return groupedRequests;
    }

    public List<List<Object>> getSortedRequests(RequestSorting sortingType){

        Map<String, Integer> requestsGroupByBooks = getRequestsGroupByBooks();
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

    public List<Book> getLongLiedBooks(LongLiedBookSorting sortingType, int numberOfMonth){
        List<Book> books = bookRepository.getBooks();
        return switch(sortingType) {
            case PRICE_DOWN -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > numberOfMonth)
                    .sorted(Comparator.comparing(Book::getPrice))
                    .toList();


            case DATE_UP -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > numberOfMonth)
                    .sorted(Comparator.comparing(Book::getAdmissionDate))
                    .toList();

            case DATE_DOWN -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > numberOfMonth)
                    .sorted(Comparator.comparing(Book::getAdmissionDate).reversed()).toList();

            case PRICE_UP -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > numberOfMonth)
                    .sorted(Comparator.comparing(Book::getPrice).reversed())
                    .toList();

            case NONE -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > numberOfMonth)
                    .toList();
        };
    }

    Book getBookById(Integer id){
        List<Book> books = bookRepository.getBooks();
        for (Book book : books) {
            if (book.getId() == id){
                return book;
            }
        }
        return null;
    }

    public String importNewBook(String filename){


        try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), StandardCharsets.UTF_8)
                )){
            String line;
            br.readLine(); // пропускаем заголовок

            while((line = br.readLine()) != null){
                String[] fields = line.split(";");
                Book checking = getBookById(parseInt(fields[0]));
                Book newBook = new Book(
                        parseInt(fields[0]), // id
                        fields[1], // title
                        fields[2], // author
                        parseInt(fields[3]), // year
                        parseBookStatus(fields[4]), // status
                        parseDouble(fields[5]), // price
                        parseBookType(fields[6])
                );
                if (checking == null) {
                    bookRepository.addNewBook(newBook);
                    bookRepository.checkMaxBookId(parseInt(fields[0]));
                }else {
                    checkOrChangeBook(checking, newBook);
                }


            }

        }
        catch (IOException | BookCanBotBeCreated | ArrayIndexOutOfBoundsException e){
            return e.getMessage();
        }
        return "";
    }


    public String exportBook(String title){

        Book book = getBookByTitle(title);
        if (book == null) return "Книга не была найдена";

        String filename = title + ".csv";
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filename),  StandardCharsets.UTF_8
                )
        )){


            String[] headers = {"ID", "Title", "Author", "Year", "Status", "Price", "Type"};
            bw.write(String.join(";", headers));
            bw.newLine();
            bw.write(getCsvBookLine(book));



        } catch (IOException e) {
           return e.getMessage();
        }

        return "";

    }

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

    private void checkOrChangeBook(Book old_, Book new_){
        boolean checking = old_.compareTo(new_)  == 0;
        if (checking) return;

        if (!old_.getTitle().equals( new_.getTitle())){
                old_.setTitle(new_.getTitle());
        }
        if (old_.getStatus() != new_.getStatus()){
            old_.setStatus(new_.getStatus());
        }

        if (old_.getLastPurchaseDate() != new_.getLastPurchaseDate()){
            old_.setLastPurchaseDate(new_.getLastPurchaseDate());
        }

        if (old_.getAdmissionDate() != new_.getAdmissionDate()){
            old_.setAdmissionDate(new_.getAdmissionDate());
        }

        if (old_.getPrice() != new_.getPrice()){
            old_.setPrice(new_.getPrice());
        }

        if (old_.getYear() != new_.getYear()){
            old_.setYear(new_.getYear());
        }

        if (old_.getGenre() != new_.getGenre()){
            old_.setGenre(new_.getGenre());
        }

        if (!old_.getAuthor().equals(new_.getAuthor())){
            old_.setAuthor(new_.getAuthor());
        }
    }

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

    public String importRequest(String filename){
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filename), StandardCharsets.UTF_8
                )
        )){
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");
                Request checking = getRequestById(parseInt(fields[0]));

                Book book = getBookById(parseInt(fields[1]));
                if (book == null) return "Для данной книги не может быть создан запрос";

                Order order = getOrderById(parseInt(fields[2]));
                if (order == null) return "Данный запрос не прикреплен к заказу";
                if (!checkConnectionRequestOrder(parseInt(fields[1]), order)) return "Заказ не имеет такого наполнения";

                Request newRequest = new Request(parseInt(fields[0]),book, order);
                if (checking == null) {
                    requestRepository.add(newRequest);
                    requestRepository.checkMaxRequestId(parseInt(fields[0]));
                }else {
                    checkOrChangeRequest(checking, newRequest);
                }

            }
        }
        catch (IOException e){
            return e.getMessage();
        }

        return "";

    }

    public String exportRequest(String id_){
        int id = parseInt(id_);
        Request request = getRequestById(id);
        if (request == null) return "Такой запрос не был найден";

        String filename = "request_" + request.getId() + ".csv";
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filename), StandardCharsets.UTF_8
                )
        )) {

            String[] headers = {"ID", "Book", "Order"};
            bw.write(String.join(";", headers));
            bw.newLine();
            bw.write(getCsvRequest(request));

        }
        catch (IOException e) {
            return e.getMessage();
        }

        return "";
    }

    private String getCsvRequest(Request request){
        return request.getId() + ";" + request.getBook().getId() + ";" + request.getOrder().getId();
    }

    private Request getRequestById(int id){
        List<Request> requests = requestRepository.getRequests();
        for (Request request: requests){
            if (request.getId() == id){
                return request;
            }
        }

        return null;
    }

    private Order getOrderById(int id){
        List<Order> orders = orderRepository.getOrders();
        for (Order order: orders){
            if (order.getId() == id){
                return order;
            }
        }
        return null;
    }

    private void checkOrChangeRequest(Request old_, Request new_){
        boolean checking = old_.compareTo(new_) == 0;
        if (checking) return;

        if (old_.getBook().getId() != new_.getBook().getId()){
            old_.setBook(new_.getBook());
        }

        if (old_.getOrder().getId() != new_.getOrder().getId()){
            old_.setOrder(new_.getOrder());
        }
    }

    // предполагается, что запрос опирается на сущетсвующий заказ, который включает в себя книгу
    private boolean checkConnectionRequestOrder(int bookId, Order order){
        for (Book book: order.getBooks()){
            if (book.getId() == bookId) return true;
        }
        return  false;

    }



}

package task_3_4.services;

import task_3_4.model.Order;
import task_3_4.model.Request;
import task_3_4.model.types.BookSorting;
import task_3_4.model.types.BookStatus;
import task_3_4.model.types.LongLiedBookSorting;
import task_3_4.model.types.RequestSorting;
import task_3_4.repositories.RequestRepository;
import task_3_4.model.Book;
import task_3_4.repositories.BookRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// service for warehouse instead of previous one
public class BookService {
    private BookRepository bookRepository;
    private RequestRepository requestRepository;

    public BookService(BookRepository bookRepository, RequestRepository requestRepository) {
        this.bookRepository = bookRepository;
        this.requestRepository = requestRepository;
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

    public List<Book> getLongLiedBooks(LongLiedBookSorting sortingType){
        List<Book> books = bookRepository.getBooks();
        return switch(sortingType) {
            case PRICE_DOWN -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > 6)
                    .sorted(Comparator.comparing(Book::getPrice))
                    .toList();


            case DATE_UP -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > 6)
                    .sorted(Comparator.comparing(Book::getAdmissionDate))
                    .toList();

            case DATE_DOWN -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > 6)
                    .sorted(Comparator.comparing(Book::getAdmissionDate).reversed()).toList();

            case PRICE_UP -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > 6)
                    .sorted(Comparator.comparing(Book::getPrice).reversed())
                    .toList();

            case NONE -> books.stream()
                    .filter(p -> p.getStatus() == BookStatus.IN_STOCK)
                    .filter(p -> ChronoUnit.MONTHS.between(p.getLastPurchaseDate(), LocalDate.now()) > 6)
                    .toList();
        };
    }




}

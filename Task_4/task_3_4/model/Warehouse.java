package task_3_4.model;

import task_3_4.model.types.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Warehouse {
    ArrayList<Book> books;
    ArrayList<Request> requests;

    private static volatile Warehouse instance;

    private  Warehouse() {
        this.books = new ArrayList<>();
        this.requests = new ArrayList<>();
        initializeData();
    }

    private void  initializeData(){
        Book book1 = new Book("Harry Potter 1", "J.K.Rowlling",
                2024, BookStatus.IN_STOCK, 1500, BookTypes.FANTASY);
        Book book2 = new Book("Peter The First", "A.Tolstoy",
                2024, BookStatus.IN_STOCK, 2100, BookTypes.HISTORY);
        Book book3 = new Book("The Great Expectations", "C.Dickens",
                2025, BookStatus.OUT_OF_STOCK, 1500, BookTypes.CLASSICAL);
        Book book4 = new Book("War and Piece", "L.Tolstoy",
                2025, BookStatus.OUT_OF_STOCK, 1500, BookTypes.CLASSICAL);
        Book book5 = new Book("Oblomov", "I.Goncharov",
                2023, BookStatus.OUT_OF_STOCK, 1400, BookTypes.HISTORY);

        books.add(book1);
        books.add(book2);
        books.add(book4);
        books.add(book5);
        books.add(book3);
    }

    public static Warehouse getInstance(){
        if (instance == null){
            synchronized (Warehouse.class){
                if (instance == null){
                    instance = new Warehouse();
                }
            }
        }
        return instance;
    }

    //added - оставить
    public ArrayList<Book> getBooks() {
        return new ArrayList<>(this.books);
    }

    //added - оставить
    public void addRequest(Request request){
        this.requests.add(request);
    }

    public void deleteRequest(Request request){
        this.requests.remove(request);
    }

















    // added
    void cancellRequestsByBook(Book book){
        this.requests.removeIf(r -> r.getBook().equals(book));
    }

    //added
    public void cancellOrderRequests(Order order){
        System.out.println("Удаление запросов, связанных с заказом");
        this.requests.removeIf(r -> r.getOrder().equals(order));
    }



    //added
    public Map<String, Integer> getRequestsGroupByBooks(){
        Map<String, Integer> groupedRequests = new HashMap<>();
        for (Request request : requests) {
            if ( !groupedRequests.containsKey(request.getBook().getTitle())){
                groupedRequests.put(request.getBook().getTitle(), 1);
            }else{
                groupedRequests.merge(request.getBook().getTitle(), 1, Integer::sum);
            }
        }

        return groupedRequests;
    }

    //added
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


    //added
    public void sortRequestsByParameter(List<List<Object>> groupedRequests, int index, boolean asc){
        Comparator<List<Object>> comparator = Comparator.comparing(
                list -> (Comparable)list.get(index)
        );
        if (!asc){
            comparator = comparator.reversed();
        }

        groupedRequests.sort(comparator);
    }

    //added
    public List<Book> getSortedBooks(BookSorting sortingType){
        List<Book> sortedBooks;
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

    //added
    public String getBookDescription(String bookName){
        String foundBook =  "Такой книги не нашлось";
        for (Book book : books) {
            if (Objects.equals(book.getTitle(), bookName)){
                foundBook = book.getDescription();
                break;
            }
        }

        return foundBook;

    }

    //added
    public void setLastPurchase(List<Book> books){
        for (Book book : books) {
            book.setLastPurchaseDate(LocalDate.now());
        }
    }

    //added
    public List<Book> getLongLiedBooks(LongLiedBookSorting sortingType){
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

    // added - оставить
    public List<Request> getRequests(){
        return new ArrayList<Request>(requests);
    }

    // added
    public void receiveBook(String title){
        Book book_= null;
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                book.setStatus(BookStatus.IN_STOCK);
                book.setAdmissionDate(LocalDate.now());
                book_ = book;
                break;
            }
        }

        assert book_ != null:"Book was not found";
        cancellRequestsByBook(book_);
    }

    //added
    Boolean checkBook(Book book){
        for(Book iBook : books){
            if (iBook.equals(book)){
                return iBook.getStatus() == BookStatus.IN_STOCK;
            }
        }

        return false;
    }




}

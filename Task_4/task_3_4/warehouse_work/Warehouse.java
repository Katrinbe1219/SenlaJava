package task_3_4.warehouse_work;

import task_3_4.books.Book;
import task_3_4.order.Order;
import task_3_4.types.BookSorting;
import task_3_4.types.BookStatus;
import task_3_4.types.LongLiedBookSorting;
import task_3_4.types.RequestSorting;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Warehouse {
    ArrayList<Book> books;
    ArrayList<Request> requests;

    public Warehouse(ArrayList<Book> books) {
        this.books = books;
        this.requests = new ArrayList<>();
    }

//    void addBook(IBook book){
//        IBook foundBook = null;
//        for (IBook iBook : books) {
//            if (iBook.equals(book)) {
//                foundBook = iBook;
//                break;
//            }
//        }
//
//        assert foundBook != null: "Не найдена книга";
//        foundBook.setStatus(BookStatus.InStock);
//    }
//
//    void removeBook(IBook book){
//        IBook foundBook = null;
//        for (IBook iBook : books) {
//            if (iBook.equals(book)) {
//                foundBook = iBook;
//                break;
//            }
//        }
//
//        assert foundBook != null: "Не найдена книга";
//        foundBook.setStatus(BookStatus.OutOfStock);
//    }

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

    Boolean checkBook(Book book){
        for(Book iBook : books){
            if (iBook.equals(book)){
                return iBook.getStatus() == BookStatus.IN_STOCK;
            }
        }

        return false;
    }

    public void addRequest(Request request){
        this.requests.add(request);
    }

    void cancellRequestsByBook(Book book){
        this.requests.removeIf(r -> r.getBook().equals(book));
    }

    public void cancellOrderRequests(Order order){
        System.out.println("Удаление запросов, связанных с заказом");
        this.requests.removeIf(r -> r.getOrder().equals(order));
    }

    public ArrayList<Book> getBooks() {
        return this.books;
    }

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



    public void sortRequestsByParameter(List<List<Object>> groupedRequests, int index, boolean asc){
        Comparator<List<Object>> comparator = Comparator.comparing(
                list -> (Comparable)list.get(index)
        );
        if (!asc){
            comparator = comparator.reversed();
        }

        groupedRequests.sort(comparator);
    }

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


    public void setLastPurchase(List<Book> books){
        for (Book book : books) {
            book.setLastPurchaseDate(LocalDate.now());
        }
    }

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




}

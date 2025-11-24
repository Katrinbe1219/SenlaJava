package task_3_4.model;

import task_3_4.model.types.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Warehouse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    ArrayList<Book> books;
    ArrayList<Request> requests;
    int countAllBooks; // добавлено, чтобы id были инкрементированы, даже если книга удалена из склада, счетсчик не уменьшается
    int countAllRequests;

    //private static volatile Warehouse instance;

    public   Warehouse() {
        this.requests = new ArrayList<>();
        this.books = new ArrayList<>();
        this.countAllBooks = 0;
        this.countAllRequests = 0;
    }

    public void  initializeData(){
        Book book1 = new Book(1,"Harry Potter 1", "J.K.Rowlling",
                2024, BookStatus.IN_STOCK, 1500, BookTypes.FANTASY);
        Book book2 = new Book(2,"Peter The First", "A.Tolstoy",
                2024, BookStatus.IN_STOCK, 2100, BookTypes.HISTORY);
        Book book3 = new Book(3,"The Great Expectations", "C.Dickens",
                2025, BookStatus.OUT_OF_STOCK, 1500, BookTypes.CLASSICAL);
        Book book4 = new Book(4,"War and Piece", "L.Tolstoy",
                2025, BookStatus.OUT_OF_STOCK, 1500, BookTypes.CLASSICAL);
        Book book5 = new Book(5,"Oblomov", "I.Goncharov",
                2023, BookStatus.OUT_OF_STOCK, 1400, BookTypes.HISTORY);

        books.add(book1);
        books.add(book2);
        books.add(book4);
        books.add(book5);
        books.add(book3);
        this.countAllBooks = 5;
        this.countAllRequests = 0;
    }

//    public static Warehouse getInstance(){
//        if (instance == null){
//            synchronized (Warehouse.class){
//                if (instance == null){
//                    instance = new Warehouse();
//                }
//            }
//        }
//        return instance;
//    }

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

    public int getCountAllBooks(){
        return this.countAllBooks;
    }

    public void addNewBook (Book book){
        this.books.add(book);
    }

    public List<Request> getRequests(){
        return new ArrayList<>(this.requests);
    }

    public int getCountAllRequests(){
        return this.countAllRequests;
    }
    public void incrementCountAllRequests(){
        this.countAllRequests++;
    }

    public void checkMaxBookId(int id){
        this.countAllBooks  = Math.max(this.countAllBooks, id);
    }

    public void checkMaxRequestId(int id){
        this.countAllRequests = Math.max(this.countAllRequests, id);
    }



}

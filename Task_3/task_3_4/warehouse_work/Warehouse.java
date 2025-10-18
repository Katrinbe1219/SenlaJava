package task_3_4.warehouse_work;

import task_3_4.books.IBook;
import task_3_4.order.Order;
import task_3_4.types.BookStatus;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Warehouse {
    ArrayList<IBook> books;
    ArrayList<Request> requests;

    public Warehouse(ArrayList<IBook> books) {
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
        IBook book_= null;
        for (IBook book : books) {
            if (book.getTitle().equals(title)) {
                book.setStatus(BookStatus.InStock);
                book_ = book;
                break;
            }
        }

        assert book_ != null:"Book was not found";
        cancellRequestsByBook(book_);
    }

    Boolean checkBook(IBook book){
        for(IBook iBook : books){
            if (iBook.equals(book)){
                return iBook.getStatus() == BookStatus.InStock;
            }
        }

        return false;
    }

    public void addRequest(Request request){
        this.requests.add(request);
    }

    void cancellRequestsByBook(IBook book){
        this.requests.removeIf(r -> r.getBook().equals(book));
    }

    public void cancellOrderRequests(Order order){
        System.out.println("Удаление запросов, связанных с заказом");
        this.requests.removeIf(r -> r.getOrder().equals(order));
    }

    public ArrayList<IBook> getBooks() {
        return this.books;
    }

}

package task_3_4.repositories;

import task_3_4.model.Book;
import task_3_4.model.Warehouse;
import java.util.ArrayList;

public class BookRepository {
    Warehouse warehouse;

    public BookRepository(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
    public ArrayList<Book> getBooks() {
        return warehouse.getBooks();
    }

    public Book getBookByTitle (String title){
        Book book_= null;
        for (Book book : getBooks()) {
            if (book.getTitle().equals(title)) {
                book_ = book;
                break;
            }
        }
        return book_;
    }

    public int getCurrentMaxId(){
        return warehouse.getCountAllBooks();
    }


    public void addNewBook(Book book){
        warehouse.addNewBook(book);
    }

    public void checkMaxBookId(int id){
        warehouse.checkMaxBookId(id);
    }


}

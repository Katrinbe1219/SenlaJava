package com.example.application.repositories;

import com.example.application.model.Book;
import com.example.application.model.Warehouse;
import com.example.custom_annotations.Inject;

import java.util.ArrayList;

@Inject
public class BookRepository {
    @Inject
    Warehouse warehouse;

//    public BookRepository(Warehouse warehouse) {
//        this.warehouse = warehouse;
//    }
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

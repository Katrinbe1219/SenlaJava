package task_3_4.books;

import task_3_4.types.BookStatus;
import task_3_4.types.BookTypes;

public class Book {
    String title;

    String author;
    int year;
    BookStatus status;
    double price;
    BookTypes genre;

    public Book(String title, String author, int year, BookStatus status, double price, BookTypes type) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.status = status;
        this.price = price;
        this.genre = type;
    }


    public void setStatus(BookStatus status) {
        this.status = status;
    }


    public BookStatus getStatus() {
        return this.status;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getTitle() {
        return this.title;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public String getAuthor() {
        return this.author;
    }


    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return this.year;
    }


    public void setPrice(double price) {
        this.price =  price;
    }


    public double getPrice() {
        return this.price;
    }


    public BookTypes getGenre() {
        return this.genre;
    }
}

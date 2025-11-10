package task_3_4.model;

import task_3_4.model.types.BookStatus;
import task_3_4.model.types.BookTypes;

import java.time.LocalDate;

public class Book {
    String title;

    String author;
    int year;
    BookStatus status;
    double price;
    BookTypes genre;
    LocalDate lastPurchaseDate;
    LocalDate admissionDate;

    public Book(String title, String author, int year, BookStatus status, double price, BookTypes type) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.status = status;
        this.price = price;
        this.genre = type;
        this.lastPurchaseDate = LocalDate.of(2025, 1,9);

        if (status == BookStatus.IN_STOCK){
            this.admissionDate = LocalDate.now();

        }

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

    public String getDescription(){
        return title + ":\n" + "The author of this books is "  + this.author +
                ".\nThe publication date is " + this.year +
                ".\nThe genre is " + this.genre + "\n"
                + "price is " + this.price + "\n";
    }

    public void setLastPurchaseDate(LocalDate date){
        this.lastPurchaseDate = date;
    }

    public LocalDate getLastPurchaseDate(){
        return this.lastPurchaseDate;
    }

    public LocalDate getAdmissionDate(){
        return this.admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate){
        this.admissionDate = admissionDate;
    }
}

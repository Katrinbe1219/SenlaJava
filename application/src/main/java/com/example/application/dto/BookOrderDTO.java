package com.example.application.dto;
import com.example.application.model.types.BookTypes;


public class BookOrderDTO {
    String title;
    double price;
    BookTypes genre;
    AuthorDTO author;

    public BookOrderDTO(String title, double price, BookTypes genre, AuthorDTO author) {
        this.title = title;
        this.price = price;
        this.genre = genre;
        this.author = author;
    }

    public BookOrderDTO() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public BookTypes getGenre() {
        return genre;
    }
    public void setGenre(BookTypes genre) {
        this.genre = genre;
    }

    public AuthorDTO getAuthor() {
        return author;
    }
    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }

}

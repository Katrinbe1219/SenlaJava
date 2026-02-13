package com.example.application.dto;

import com.example.application.model.types.BookStatus;
import com.example.application.model.types.BookTypes;

import java.time.LocalDate;

public class BookDTO {
    BookStatus status;
    String title;
    int year;
    double price;
    BookTypes genre;
    AuthorDTO author;
    String lastPurchaseDate;
    String admissionDate;

    public BookDTO() {}

    public BookDTO(BookStatus status, String title, int year, double price, BookTypes genre, AuthorDTO author, LocalDate lastPurchaseDate, LocalDate admissionDate) {
        this.status = status;
        this.title = title;
        this.year = year;
        this.price = price;
        this.genre = genre;
        this.author = author;
        this.lastPurchaseDate = String.valueOf(lastPurchaseDate);
        this.admissionDate = String.valueOf(admissionDate);
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


    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }
    public AuthorDTO getAuthor() {
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
    public void setGenre(BookTypes genre) {
        this.genre = genre;
    }

    public void setLastPurchaseDate(LocalDate date){
        this.lastPurchaseDate = String.valueOf(date);
    }
    public String getLastPurchaseDate(){
        return this.lastPurchaseDate;
    }

    public String getAdmissionDate(){
        return this.admissionDate;
    }
    public void setAdmissionDate(LocalDate admissionDate){

        this.admissionDate = String.valueOf(admissionDate);
    }



}

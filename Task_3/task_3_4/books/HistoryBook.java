package task_3_4.books;

import task_3_4.types.BookStatus;

public class HistoryBook implements IBook{
    String title;
    String author;
    int year;
    BookStatus status;
    double price;
    String genre = "historical";

    public HistoryBook(String title, String author, int year, BookStatus status, double price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.status = status;
        this.price = price;
    }

    @Override
    public void setStatus(BookStatus status) {
        this.status = status;
    }

    @Override
    public BookStatus getStatus() {
        return this.status;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public void setPrice(double price) {
        this.price =  price;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public String getGenre() {
        return this.genre;
    }
}

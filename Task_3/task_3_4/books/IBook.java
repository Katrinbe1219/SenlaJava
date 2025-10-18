package task_3_4.books;

import task_3_4.types.BookStatus;

public interface IBook {
    void setStatus(BookStatus status);
    BookStatus getStatus();

    void setTitle(String title);
    String getTitle();

    void setAuthor(String author);
    String getAuthor();

    void setYear(int year);
    int getYear();

    void setPrice(double price);
    double getPrice();

    String getGenre();

}

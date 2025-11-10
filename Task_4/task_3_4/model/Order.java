package task_3_4.model;

import task_3_4.model.types.BookStatus;
import task_3_4.model.types.OrderStatus;

import java.time.LocalDate;
import java.util.ArrayList;

public class Order {
    OrderStatus status;
    Customer customer;
    ArrayList<Book> books;
    double totalCost;
    LocalDate completionDate;

    public Order(){
        books = new ArrayList<>();
        totalCost = 0;
        status = OrderStatus.NEW;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }
    public OrderStatus getStatus(){
        return this.status;
    }

    public void setCustomer(Customer customer){
        this.customer = customer;
    }

    Customer getCustomer(){
        return this.customer;
    }

    public void addBook(Book book){
        this.books.add(book);
        totalCost += book.getPrice();

    }

    public void delBook(Book book){
        this.books.remove(book);
        totalCost -= book.getPrice();
    }

    public double getTotalCost(){
        return this.totalCost;
    }

    public ArrayList<Book> getBooks(){
        return this.books;
    }

    public OrderStatus checkUpdateByBook(String bookTitle){
        int countWaitings = 0;
        for (Book book : this.books){
            if (book.getStatus() == BookStatus.OUT_OF_STOCK){
                countWaitings ++;
            }
        }

        if (countWaitings == 0){
            setCompletionDate(LocalDate.now());
            setStatus(OrderStatus.DONE);
            return OrderStatus.DONE;
        }else{
            return OrderStatus.NEW;
        }
    }

    public void setCompletionDate(LocalDate completionDate){
        this.completionDate = completionDate;
    }

    public LocalDate getCompletionDate(){
        return this.completionDate;
    }

    @Override
    public String toString(){
        String status = switch(this.status){
            case NEW -> "New";
            case DONE -> "Done";
            case null, default -> "Проблема";
        };

        StringBuilder booksInfo = new StringBuilder();
        for (Book book: this.books){

            booksInfo.append(book.getDescription());
        }

        return "Customer: " + getCustomer().toString() + "\n" +
                 "Status: " + status + "\n" +
                "Books: " + booksInfo +
                "Price: " + this.totalCost + "\n";
    }


}

package task_3_4.order;

import task_3_4.books.Book;
import task_3_4.customer.Customer;
import task_3_4.types.BookStatus;
import task_3_4.types.OrderStatus;

import java.util.ArrayList;

public class Order {
    OrderStatus status;
    Customer customer;
    ArrayList<Book> books;
    double totalCost;

    public Order(){
        System.out.println("Создается заказ");
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
        System.out.println("Указан покупатель - " + customer.toString());
    }
    Customer getCustomer(){
        return this.customer;
    }

    public void addBook(Book book){
        this.books.add(book);
        totalCost += book.getPrice();
        System.out.println("Добавлена книга " + book.getTitle());
        System.out.println("Сумма заказа " + this.totalCost);
    }

    public void delBook(Book book){
        this.books.remove(book);
        totalCost -= book.getPrice();
        System.out.println("Удалена книга " + book.getTitle());
        System.out.println("Сумма заказа " + this.totalCost);
    }

    public double getTotalCost(){
        return this.totalCost;
    }

    public ArrayList<Book> getBooks(){
        return this.books;
    }

    public void checkUpdateByBook(String bookTitle){
        System.out.println("Заказ проверяется из-за получения книги " + bookTitle);
        int countWaitings = 0;
        for (Book book : this.books){
            if (book.getStatus() == BookStatus.OUT_OF_STOCK){
                countWaitings ++;
            }
        }

        if (countWaitings == 0){
            System.out.println("Заказ завершен");
            setStatus(OrderStatus.DONE);
        }else{
            System.out.println("Заказ все еще не может быть завершен");
        }
    }



}

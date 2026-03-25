package com.example.application.model;

import com.example.application.model.converters.BookTypesConverter;
import com.example.application.model.converters.ConverterOrderStatus;
import com.example.application.model.types.BookStatus;
import com.example.application.model.types.OrderStatus;
import jakarta.persistence.*;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity(name="orders")
public class Order implements Comparable<Order>, Serializable {

    @Convert(converter = ConverterOrderStatus.class)
    @Column(name = "status")
    OrderStatus status;

    // CAscade Persist опасно ставить, так как без заказа покупатель существует
    @ManyToOne(fetch = FetchType.LAZY) // LAZY - покупатель не загружается авто при загрузке order
   @JoinColumn(name = "customer_id")
    Customer customer;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(
            name = "order_books",
            joinColumns = @JoinColumn(name="order_id"),
            inverseJoinColumns = @JoinColumn(name="book_id")
    )
    List<Book> books;

    @Transient
    double totalCost;

    @Column(name="completion_date")
    LocalDate completionDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    int id;

    public Order(int id){
        this.id = id;
        books = new ArrayList<>();
        totalCost = 0;
        status = OrderStatus.NEW;
    }
    public Order(int id, Customer c){
        this.customer = c;
        this.id = id;
        books = new ArrayList<>();
        totalCost = 0;
        status = OrderStatus.NEW;
    }
    public Order(){
        books = new ArrayList<>();
        totalCost = 0;
        status = OrderStatus.NEW;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setId(int id){
        this.id = id;
    }
    public Integer getId() {
        return this.id;
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

    public Customer getCustomer(){
        return this.customer;
    }

    public void addBook(Book book){
        this.books.add(book);
        totalCost += book.getPrice();

    }

    public void addBook(Book book, boolean condition){
        this.books.add(book);
    }

    public void delBook(Book book){
        this.books.remove(book);
        totalCost -= book.getPrice();
    }

    public double getTotalCost(){
        return this.totalCost;
    }

    public List<Book> getBooks(){
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

        return "ID: "+ this.id + "\n"
                + "Customer: " + getCustomer().toString() + "\n" +
                 "Status: " + status + "\n" +

                "Books: " + booksInfo +

                "\n\nTOTAL PRICE: " + this.totalCost + "\n" +
                "Completion Date " + this.completionDate + "\n";
    }


    @Override
    public int compareTo(Order o) {
        return Comparator
                .comparing(Order::getId, Comparator.nullsFirst(Integer::compareTo))
                .thenComparing(Order::getStatus, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Order::getCompletionDate, Comparator.nullsFirst(LocalDate::compareTo))
                .thenComparing(Order::getTotalCost)
                .thenComparing(Order::getCustomer, Comparator.nullsFirst(Customer::compareTo))
                .thenComparing(this::getSortedBookIds, Comparator.nullsFirst(this::compareBookIdLists))
                .compare(this,o);
    }

    private List<Integer> getSortedBookIds(Order order){
        if (order.getBooks() == null) return null;
        return order.getBooks().stream()
                .map(Book::getId)
                .sorted().toList();
    }

    private int compareBookIdLists(List<Integer> list1, List<Integer> list2){
        if (list1==null && list2==null) return 0;

        if (list1==null) return -1;
        if (list2==null) return 1;
        int miniSize = Math.min(list1.size(), list2.size());

        for (int i=0; i< miniSize; i++){
            int comparison = Integer.compare(list1.get(i), list2.get(i));
            if (comparison != 0) return comparison;
        }

        return Integer.compare(list1.size(), list2.size());
    }

    public int compareList(Order o){
        List<Integer> list1 = getSortedBookIds(this);
        List<Integer> list2 = getSortedBookIds(o);
        return  compareBookIdLists(list1, list2);
    }

    public void clearBooks(){
        books.clear();
        this.totalCost = 0;
    }

    public String getCsvBooks(){
        StringBuilder res = new StringBuilder(String.valueOf(this.books.getFirst().getId()));
        for (int i=1; i< books.size(); i++){
            res.append(";").append(books.get(i).getId());
        }

        return res.toString();
    }

    public void countTotalCost(){
        this.totalCost = 0;
        for (Book book : this.books){
            this.totalCost += book.getPrice();
        }
    }
}

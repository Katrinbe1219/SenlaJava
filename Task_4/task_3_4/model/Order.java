package task_3_4.model;

import task_3_4.model.types.BookStatus;
import task_3_4.model.types.OrderStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Order implements Comparable<Order>, Serializable {
    OrderStatus status;
    Customer customer;
    ArrayList<Book> books;
    double totalCost;
    LocalDate completionDate;
    int id;

    public Order(int id){
        this.id = id;
        books = new ArrayList<>();
        totalCost = 0;
        status = OrderStatus.NEW;
    }



    public int getId() {
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

        return "ID: "+ this.id + "\n"
                + "Customer: " + getCustomer().toString() + "\n" +
                 "Status: " + status + "\n" +
                "Books: " + booksInfo +
                "Price: " + this.totalCost + "\n";
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
}

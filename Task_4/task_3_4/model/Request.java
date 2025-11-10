package task_3_4.model;

public class Request {
    private Book book;
    private Order order;


    public Request(Book book, Order order){
        this.book = book;
        this.order = order;
    }

    public Order getOrder(){
        return this.order;
    }

    public Book getBook(){
        return this.book;
    }
}

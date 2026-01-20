package com.example.application.services;

import com.example.application.exceptions.OrderCanNotBeCreated;
import com.example.application.model.Book;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.model.types.OrderStatus;
import com.example.application.repositories.BookRepository;
import com.example.application.repositories.OrderRepository;
import com.example.custom_applications.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

//ID;Name;Surname;Email;TotalCost;Status;CompletionDate;Books
@Inject
public class OrderFileService {
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private BookRepository bookRepository;

//    public OrderFileService(OrderRepository orderRepository, BookRepository bookRepository) {
//        this.orderRepository = orderRepository;
//        this.bookRepository = bookRepository;
//    }

    public String importOrder(String filename)  {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filename), StandardCharsets.UTF_8
                )
        )
        ) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");
                Order order = getOrderById(parseInt(fields[0]));
                Order newOrder = createOrder(fields);
                if (order == null){
                        orderRepository.addOrder(newOrder);
                        orderRepository.checkMaxId(parseInt(fields[0]));
                }else {

                    checkOrChange(order, newOrder);
                }
            }

        } catch (IOException | OrderCanNotBeCreated | ArrayIndexOutOfBoundsException e) {
            return e.getMessage();
        }

        return "";
    }

    private Order getOrderById(int id) {
        List<Order> orders= orderRepository.getOrders();
        for (Order order: orders){
            if (order.getId() == id){
                return order;
            }
        }

        return null;
    }

    private Order createOrder(String[] fields)  throws OrderCanNotBeCreated {

        Order order  = new Order(parseInt(fields[0]));

        Customer customer = new Customer(fields[1], fields[2], fields[3]);
        order.setCustomer(customer);
        String[] booksIds = fields[7].split(",");
        Book foundBook;
        for (String bookId: booksIds) {
            foundBook = getBookById(Integer.parseInt(bookId));
            if (foundBook == null) throw new OrderCanNotBeCreated("Не существует книги");
            order.addBook(foundBook);

            order.setStatus(parseStatus(fields[5]));

            if (order.getStatus() == OrderStatus.DONE){
                order.setCompletionDate(parseDate(fields[6]));
            }
        }

        return order;




    }

    private Integer parseInt(String id) throws OrderCanNotBeCreated {
        try {
            return Integer.parseInt(id);
        }catch (NumberFormatException e){
            throw new OrderCanNotBeCreated(e.getMessage());
        }

    }

    private Double parseDouble(String id) throws OrderCanNotBeCreated {
        try{
            return Double.parseDouble(id);
        }catch (NumberFormatException e){
            throw new OrderCanNotBeCreated(e.getMessage());
        }
    }

    private Book getBookById(int id) {
        List<Book> books = bookRepository.getBooks();
        for (Book book: books){
            if (book.getId() == id){
                return book;
            }
        }


        return null;
    }

    private OrderStatus parseStatus(String status) throws OrderCanNotBeCreated {
        return switch (status){
            case "new" -> OrderStatus.NEW;
            case "done" -> OrderStatus.DONE;
            case "cancelled" -> OrderStatus.CANCELLED;
            default -> throw new OrderCanNotBeCreated("Не существует статус");
        };
    }

    private LocalDate parseDate (String date) throws OrderCanNotBeCreated {
        try {
            return LocalDate.parse(date);
        }catch (DateTimeParseException e){
            throw  new OrderCanNotBeCreated("Дата некорректна");
        }
    }

    private void checkOrChange(Order old_, Order new_){
        boolean checking = old_.compareTo(new_) == 0;
        if (checking) return;

        if (old_.getStatus() != new_.getStatus()){
            old_.setStatus(new_.getStatus());
        }

        if (new_.getCompletionDate() != old_.getCompletionDate()){
            old_.setCompletionDate(new_.getCompletionDate());
        }

        if (old_.getCustomer().compareTo(new_.getCustomer()) != 0){
            old_.setCustomer(new_.getCustomer());
        }

       if  (old_.compareList(new_)!= 0){
           old_.clearBooks();
           List<Book> books = new_.getBooks();
           for (Book book: books){
               old_.addBook(book);
           }
       }



    }

    public String exportOrder(String id_){
        int id = parseInt(id_);
        Order order = getOrderById(id);
        if (order == null) return  "Заказ не найден";

        try  (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream("order_" + id_ + ".csv"), StandardCharsets.UTF_8
                )
        )){
            String headers = "ID;Name;Surname;Email;TotalCost;Status;CompletionDate;Books";
            bw.write(headers);
            bw.newLine();
            bw.write(getCsvOrder(order));
        }
        catch (IOException e){
            return e.getMessage();
        }

        return "";
    }

    private String getCsvOrder(Order order){
        return order.getId() + ";" + order.getCustomer().getCsvInfo() + ";"
                + order.getTotalCost() + ";" + getCsvStatus(order.getStatus()) + ";"
                + order.getCompletionDate() + ";" + order.getCsvBooks() ;
    }

    private String getCsvStatus(OrderStatus status){
        return switch (status){
            case NEW -> "NEW";
            case DONE -> "DONE";
            case CANCELLED -> "CANCELLED";
        };
    }
}

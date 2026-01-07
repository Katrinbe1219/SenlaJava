package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Author;
import com.example.application.model.Book;
import com.example.application.model.OrderBooks;
import com.example.application.model.types.BookStatus;
import com.example.application.model.types.BookTypes;
import com.example.custom_annotations.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Inject
public class OrderBooksImplementation extends AbstractDao<OrderBooks, Integer>{

    @Inject
    Connection connection;

    @Override
    protected OrderBooks mapRow(ResultSet resultSet) throws CanNotMakeExecution {
        //order_book_id | order_id | book_id
        OrderBooks orderBooks = new OrderBooks();
        try {
            orderBooks.setBookID(resultSet.getInt("book_id"));
            orderBooks.setOrderID(resultSet.getInt("order_id"));
            orderBooks.setOrderBookId(resultSet.getInt("order_book_id"));
            return orderBooks;
        }catch (Exception e) {
            throw new CanNotMakeExecution("Проблема при mapRow orderBooks " + e.getMessage());
        }

    }

    private Book mapBookRow(ResultSet resultSet) throws CanNotMakeExecution {
        Book book = new Book();
        try {
            // b.book_id, b.title, g.genre_name, a.name, a.paternal, a.surname, a.author_id, b.year, b.status, b.price, b.last_date_purchase, b.admission_date
            book.setId(resultSet.getInt(1));
            book.setTitle(resultSet.getString(2));
            book.setGenre(getGenre(resultSet.getString(3)));
            book.setAuthor(getAuthor(resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getLong(7)));
            book.setYear(resultSet.getInt(8));
            book.setStatus(getStatus(resultSet.getString(9)));
            book.setPrice(resultSet.getDouble(10));
            book.setLastPurchaseDate(resultSet.getObject(11, LocalDate.class));

            return book;

        } catch (SQLException e) {
            throw new CanNotMakeExecution("Problem during mapRow " +  e.getMessage());
        }
    }

    @Override
    protected Integer getId(OrderBooks orderBooks) throws CanNotMakeExecution {
        return 0;
    }

    @Override
    protected OrderBooks insert(OrderBooks orderBooks) throws CanNotMakeExecution {
        return null;
    }

    @Override
    protected String getTableName() throws CanNotMakeExecution {
        return "order_books";
    }

    @Override
    protected Connection getConnection() throws CanNotMakeExecution {
        return this.connection;
    }

    @Override
    protected OrderBooks update(OrderBooks orderBooks) throws CanNotMakeExecution {
        return null;
    }

    public List<Book> getOrdersBook(int order_id) throws CanNotMakeExecution {
        List<Book> orderBooksList = new ArrayList<Book>();

        String sql = "SELECT b.book_id, b.title, g.genre_name, a.name, a.paternal, a.surname, a.author_id,b.year, b.status, b.price, b.last_date_purchase, b.admission_date FROM order_books AS ob INNER JOIN books AS b  ON b.book_id = ob.book_id INNER JOIN genres AS g ON g.genre_id = b.genre_id INNER JOIN authors AS a ON a.author_id = b.author_id WHERE ob.order_id = ?; ";
        try (
                PreparedStatement pr = getConnection().prepareStatement(sql)
        ){
            pr.setInt(1, order_id);
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                orderBooksList.add(mapBookRow(rs));
            }
            return orderBooksList;
        }
        catch (Exception e) {
            throw new CanNotMakeExecution("Проблема при получении книг заказа: " + e.getMessage());
        }
    }

    public List<Book> addOrderBooks(List<Book> books, int order_id) throws CanNotMakeExecution {
        // эта функция является частью транзакции в BookShopFacade, так что не начинаем ее здесь
        // если здесь будет проблема, то rollback сработает в основной функции в BookShopFacade createOrder
        // connection один на всю программу
        String sql = "INSERT INTO order_books (order_id, book_id) VALUES (?,?)";

        try (PreparedStatement pr = getConnection().prepareStatement(sql)){
            for (Book book : books) {
                pr.setInt(1, order_id);
                pr.setInt(2, book.getId());
                pr.addBatch();
            }

            int [] results = pr.executeBatch();
            if (results.length  == books.size()) {
                return books;
            }

            throw new CanNotMakeExecution("Не все книги были добавлены в бд. Проблема на сервер");
        }
        catch (SQLException e){
            throw new CanNotMakeExecution("Проблема при добавлении книг в бд: " + e.getMessage());
        }

    }


    private Author getAuthor (String name, String surname, String paternal, long id) throws CanNotMakeExecution {
        return new Author(name, surname, paternal, id);

    }
    private BookTypes getGenre(String id){
        // кастыльно, но предполагается, что только таких три жанра, поэтому так прописано
        return switch (id){
            case "фэнтази" -> BookTypes.FANTASY;
            case "история" -> BookTypes.HISTORY;
            case "классика" -> BookTypes.CLASSICAL;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }
    private BookStatus getStatus(String status) throws CanNotMakeExecution {
        return switch (status){
            case "I" -> BookStatus.IN_STOCK;
            case "O" -> BookStatus.OUT_OF_STOCK;
            default -> throw new CanNotMakeExecution("Нет такого статуса");
        };
    }
}

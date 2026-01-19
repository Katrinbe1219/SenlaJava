package com.example.application.dao;
import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Author;
import com.example.application.model.types.BookStatus;
import com.example.application.model.types.BookTypes;
import com.example.custom_applications.Inject;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.application.model.Book;
import org.apache.logging.log4j.Logger;

@Inject
public class BookImplementation extends AbstractDao<Book, Integer> {
    //book_id |       title        | genre_id | author_id | year | status | price | last_date_purchase | admission_date
    //getBooks = findAll
    //getBookByTitle
    //transaction -> get list of Books to delete

    @Inject
    private Connection connection;

    @Override
    public Optional<List<Book>> findAll(Logger logger) throws CanNotMakeExecution{
        String sql = "SELECT b.book_id, b.title, g.genre_name, a.name, a.paternal, a.surname, a.author_id,b.year, b.status, b.price, b.last_date_purchase, b.admission_date FROM books AS b INNER JOIN genres AS g ON g.genre_id = b.genre_id INNER JOIN authors AS a ON a.author_id = b.author_id;";
        List<Book> books = new ArrayList<>();
        try (
                Statement pr = getConnection().createStatement();
                )
        {
            ResultSet res = pr.executeQuery(sql);
            if (res.next()){
                books.add(mapRow(res, logger));
                while(res.next()) {
                    books.add(mapRow(res, logger));
                }
                return Optional.of(books);
            }
            return Optional.empty();


        }
        catch (SQLException e) {
            logger.error("Проблема findAll in BookImpl: " + e.getMessage());
            throw new CanNotMakeExecution(e.getMessage());
        }
    }

    public Optional<Book> getByTitle(String title, Logger logger) throws CanNotMakeExecution {
        String sql = "SELECT b.book_id, b.title, g.genre_name, a.name, a.paternal, a.surname, a.author_id,b.year, b.status, b.price, b.last_date_purchase, b.admission_date FROM books AS b INNER JOIN genres AS g ON g.genre_id = b.genre_id INNER JOIN authors AS a ON a.author_id = b.author_id WHERE b.title = ?;";

        try (
                PreparedStatement pr = getConnection().prepareStatement(sql)
                )
        {
            pr.setString(1, title);
            ResultSet res = pr.executeQuery();
            if (res.next()){
                return Optional.of(mapRow(res, logger));
            }
            return Optional.empty();


        } catch (Exception e) {
            logger.error("Проблема getByTitle in BookImpl: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при получении книги по названию "+ e.getMessage());
        }

    }

    public Optional<List<Book>> getLongLiedBooks(int months, String field, String descCondition, Logger logger) throws CanNotMakeExecution {
        String sql =  "SELECT  b.book_id, b.title, g.genre_name, a.name, a.paternal, a.surname, a.author_id, b.year, b.status, b.price, b.last_date_purchase, b.admission_date FROM books  AS b "  +
                "INNER JOIN genres AS g ON g.genre_id = b.genre_id INNer JOIN authors AS a ON a.author_id = b.author_id " +
                "WHERE EXTRACT(YEAR FROM AGE(?, last_date_purchase)) > ? "
                + "ORDER BY " + field + " " + descCondition;
        try (PreparedStatement pr = getConnection().prepareStatement(sql)){
            pr.setDate(1, Date.valueOf(LocalDate.now()));
            pr.setInt(2, months);

            ResultSet res = pr.executeQuery();
            List<Book> books = new ArrayList<>();

            while (res.next()) {
                books.add(mapRow(res, logger));
            }
            return Optional.of(books);

        } catch (Exception e) {
            logger.error("Проблема getLongLiedBooks in BookImpl ^ " + e.getMessage());
            throw new CanNotMakeExecution(e.getMessage());
        }

    }

    public Optional<List<Book>> getSortedBooks(String field, String descCondition, Logger logger) throws CanNotMakeExecution{
        String sql;
        if (field.equals("status")){
            sql = "SELECT  b.book_id, b.title, g.genre_name, a.name, a.paternal, a.surname, a.author_id, b.year, b.status, b.price, b.last_date_purchase, b.admission_date FROM books  AS b "  +
                    "INNER JOIN genres AS g ON g.genre_id = b.genre_id INNer JOIN authors AS a ON a.author_id = b.author_id " +
                    "WHERE status = 'I'" ;
        }else{
            sql = "SELECT  b.book_id, b.title, g.genre_name, a.name, a.paternal, a.surname, a.author_id, b.year, b.status, b.price, b.last_date_purchase, b.admission_date FROM books  AS b "  +
                    "INNER JOIN genres AS g ON g.genre_id = b.genre_id INNer JOIN authors AS a ON a.author_id = b.author_id " +
                    "ORDER BY " + field + " " + descCondition ;
        }

        try (Statement pr = getConnection().createStatement()){
            ResultSet res = pr.executeQuery(sql);
            List<Book> books = new ArrayList<>();
            while (res.next()) {
                books.add(mapRow(res, logger));
            }
            return Optional.of(books);
        }
        catch (SQLException e) {
            logger.error("Проблема getSortedBooks in BookImpl: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при сортировке: " + e.getMessage());
        }
    }
    public void save (String title, Logger logger) throws CanNotMakeExecution {
        // так как импорт выключен, ввиду наличия бд, то новые книги поступать здесь не могут
        // следовательно, только изменение информации о книги -> завоз, изменение статуса и даты
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            Optional<Book> book_  = getByTitle(title, logger);
            if (book_.isPresent()) {
                // не передаю сюда connection, так как на одну программу выдается один обьект Connection
                update(book_.get(), logger);
                conn.commit();
            }else{
                conn.rollback();
                System.out.println("Book was not found");
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            }catch (SQLException e1){
                logger.error("rollback не работает " + e1.getMessage());
            }
            throw new CanNotMakeExecution("Проблема при обновлении книги " +e.getMessage());
        }finally {
            try{
                conn.setAutoCommit(true);
            }catch (SQLException e){
                logger.error("Проблема при настройке AutoCommit " +e.getMessage());
            }
        }





    }

    @Override
    protected Book mapRow(ResultSet resultSet, Logger logger) throws CanNotMakeExecution {
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
    protected Integer getId(Book book, Logger logger) throws CanNotMakeExecution {
        // в логике данного приложения, Book всегда содержит id, поэтому отдельный BookDTO не создавался
        // по логике приложения, идти в бд за id е нужно, но уже должно быть в book Object
        return book.getId();
    }


    @Override
    protected Book insert(Book book, Logger logger) throws CanNotMakeExecution {
        // здесь такая функция не требуется, не реализуется
        // на и не используется нигде, метод save переписан
        return null;
    }

    @Override
    protected String getTableName() throws CanNotMakeExecution {
        return "book";
    }

    @Override
    protected Connection getConnection() throws CanNotMakeExecution {
        return this.connection;
    }

    @Override
    protected Book update(Book book_, Logger logger) throws CanNotMakeExecution {

        String sql = "UPDATE books SET status ='I', admission_date = ? WHERE books.book_id = ?";

        try (PreparedStatement pr = getConnection().prepareStatement(sql)){

            pr.setObject(1,LocalDate.now());
            pr.setInt(2,book_.getId());
            pr.executeUpdate();
            return book_;

        }catch (SQLException e) {
            throw new CanNotMakeExecution(e.getMessage());
        }

    }

    private BookStatus getStatus(String status) throws CanNotMakeExecution {
        return switch (status){
            case "I" -> BookStatus.IN_STOCK;
            case "O" -> BookStatus.OUT_OF_STOCK;
            default -> throw new CanNotMakeExecution("Нет такого статуса");
        };
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


    public List<Book> updateBooksLastPurchase(List<Book> book_) throws CanNotMakeExecution {
        String sql = "UPDATE books SET last_date_purchase = ? WHERE books.book_id = ?";
        try (PreparedStatement pr = getConnection().prepareStatement(sql)){
            for (Book book: book_){
                book.setLastPurchaseDate(LocalDate.now());
                pr.setObject(1, book.getLastPurchaseDate());
                pr.setObject(2, book.getId());
                pr.addBatch();
            }

            int[] result = pr.executeBatch();
            if (result.length == book_.size()){
                return book_;
            }
            throw new CanNotMakeExecution("Проблема при обновлении даты покупки книги");

        }
        catch (SQLException e) {
            throw new CanNotMakeExecution("Пробелма при обновлении даты " + e.getMessage());
        }
    }
}

///usr/lib/jvm/java-21-openjdk-amd64/bin/java   -cp "new_out/application:new_out/custom_annotations:new_out/processing_annotations:\


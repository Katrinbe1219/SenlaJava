package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.model.types.OrderStatus;
import com.example.custom_applications.Inject;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Inject
public class OrderImplementation extends AbstractDao<Order, Integer> {

    @Inject
    Connection connection;

    @Override
    public Optional<List<Order>> findAll(Logger logger) throws CanNotMakeExecution{
        String sql  =" SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id;";
        List<Order> items = new ArrayList<>();
        try (
                Statement st = getConnection().createStatement();

        ){
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                items.add(mapRow(rs, logger));
                while (rs.next()) {
                    items.add(mapRow(rs, logger));
                }
                return Optional.of(items);
            }
            return Optional.empty();
        }
        catch (SQLException e) {
            logger.error("Проблема findAll OrderIMpl: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при нахождении обьектов  " + e.getMessage());
        }
    }

    @Override
    protected Order mapRow(ResultSet resultSet, Logger logger) throws CanNotMakeExecution {
        //order_id | name | surname | email | completion_date | status
        Order order = new Order();
        try {

            order.setId(resultSet.getInt("order_id"));
            order.setCustomer(getCustomer(resultSet.getString("name"), resultSet.getString("surname"), resultSet.getString("email")));
            order.setCompletionDate(resultSet.getObject("completion_date", LocalDate.class));
            order.setStatus(getStatus(resultSet.getString("status")));

            return order;
        } catch (SQLException e) {
            logger.error("Проблема mapRow OrderIMpl: " + e.getMessage());
            throw  new CanNotMakeExecution("Проблема в mapRow Orders: " + e.getMessage());
        }
    }

    protected Order mapRow(ResultSet resultSet, boolean isTotalCost, Logger logger) throws CanNotMakeExecution {
        //order_id | name | surname | email | completion_date | status } sum(b.price)
        Order order = new Order();
        try {

            order.setId(resultSet.getInt("order_id"));
            order.setCustomer(getCustomer(resultSet.getString("name"), resultSet.getString("surname"), resultSet.getString("email")));
            order.setCompletionDate(resultSet.getObject("completion_date", LocalDate.class));
            order.setStatus(getStatus(resultSet.getString("status")));
            order.setTotalCost(resultSet.getDouble("total_cost"));

            return order;
        } catch (SQLException e) {
            logger.error("Проблема findAll OrderIMpl: " + e.getMessage());
            throw  new CanNotMakeExecution("Проблема в mapRow Orders: " + e.getMessage());
        }
    }

    @Override
    protected Integer getId(Order order, Logger logger) throws CanNotMakeExecution {
        if (order.getId() == null) {
            return null;
        }
        String sql = "SELECT order_id FROM " + getTableName() + " WHERE order_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, order.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("order_id");
            }
            return null;
        }
        catch (SQLException e) {
            logger.error("Проблема findAll OrderIMpl: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при получении id у order: "+ e.getMessage());
        }
    }

    @Override
    protected Order insert(Order order, Logger logger) throws CanNotMakeExecution {
        String sql = "INSERT INTO orders (customer_id, completion_date, status) VALUES (?,?,?);";
        try (
                PreparedStatement pr = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pr.setInt(1, order.getCustomer().getCustomerId());
            pr.setObject(2, order.getCompletionDate());
            pr.setString(3, getStatusChar(order.getStatus()));
            int affectedRows = pr.executeUpdate();

            if (affectedRows == 0) {
                throw new CanNotMakeExecution("Добавление не произошло успешно. Вставка не сработала");
            }

            ResultSet rs = pr.getGeneratedKeys();
            if (rs.next()) {
                order.setId(rs.getInt(1));
                return order;
            }else{
                throw new CanNotMakeExecution("Добавление не прошло успешно, не был добавлен ID");
            }


        } catch (Exception e) {
            logger.error("Проблема insert OrderIMpl: " + e.getMessage());
            throw new CanNotMakeExecution(e.getMessage());
        }
    }

    @Override
    protected String getTableName() throws CanNotMakeExecution {
        return "orders";
    }

    @Override
    protected Connection getConnection() throws CanNotMakeExecution {
        return this.connection;
    }

    @Override
    protected Order update(Order order, Logger logger) throws CanNotMakeExecution {
        // обновляется только статус, и в некоторых случаях дата завершения
        String sql = "UPDATE orders SET  completion_date = ?, status = ? WHERE order_id = ?";
        try (
                PreparedStatement pr = getConnection().prepareStatement(sql)

        ){
            pr.setObject(1, order.getCompletionDate());
            pr.setString(2, getStatusChar(order.getStatus()));
            pr.setInt(3, order.getId());
            int affectedRows = pr.executeUpdate();
            if (affectedRows == 0) {
                throw new CanNotMakeExecution("Обновление не было выполнено, affectedRows = 0");
            }
            return order;

        }catch (SQLException e) {
            logger.error("Проблема update OrderIMpl: " + e.getMessage());
            throw new CanNotMakeExecution("Обновление заказа не было выполнено " + e.getMessage());
        }

    }

    public List<Order> getOrders(Logger logger){
        Optional<List<Order>> orders_ = findAll(logger);
        return orders_.orElse(null);
    }

    public Optional<List<Order>> getSortedDoneOrders(LocalDate start, LocalDate end, String field, String descCondition, Logger logger) throws CanNotMakeExecution{
        String sql;
        if (field.equals("total_cost")){
            sql = " SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status, order_totals.total_cost" +
                    " FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id " +
                    "INNER JOIn (" +
                    " SELECT ob.order_id, SUM(b.price) AS total_cost FROM order_books AS ob INNER JOIN books AS b ON b.book_id = ob.book_id " +
                    " GROUP By ob.order_id" +
                    ") AS order_totals ON order_totals.order_id = o.order_id "+
                    "WHERE o.status= 'D' AND completion_date BETWEEN ? AND ? ORDER BY " + field + " " + descCondition;

        }else {
            sql = " SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status" +
                " FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id " +
                "WHERE o.status= 'D' AND completion_date BETWEEN ? AND ? ORDER BY " + field + " " + descCondition;
        }

        try (PreparedStatement pr = getConnection().prepareStatement(sql)){

            pr.setDate(1, Date.valueOf(start));
            pr.setDate(2, Date.valueOf(end));

            ResultSet resultSet = pr.executeQuery();

            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                if (field.equals("total_cost")){
                    orders.add(mapRow(resultSet, true, logger));
                }else {
                    orders.add(mapRow(resultSet, logger));
                }

            }
            return Optional.of(orders);
        }
        catch (SQLException e) {
            logger.error("Проблема getSortedDoneOrders OrderIMpl: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при получение заказов в диапазоне" + e.getMessage());
        }
    }

    public Optional<List<Order>> getOrdersInDiapazon(LocalDate start, LocalDate end, Logger logger) throws CanNotMakeExecution{
        //SELECT sum(b.price), o.order_id FROM orders AS o INNER JOIN order_books AS ob ON ob.order_id = o.order_id INNER JOIN books AS b ON ob.book_id = b.book_id GROUP BY o.order_id;
        String sql = " SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status" +
                " FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id " +
                "WHERE status= 'D' AND completion_date BETWEEN ? AND ?";


        try (PreparedStatement pr = getConnection().prepareStatement(sql)){
            pr.setDate(1, Date.valueOf(start));
            pr.setDate(2, Date.valueOf(end));
            ResultSet resultSet = pr.executeQuery();

            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(mapRow(resultSet, logger));
            }
            return Optional.of(orders);
        }
        catch (SQLException e) {
            logger.error("Проблема getOrdersInDiapazon OrderIMpl: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при получение заказов в диапазоне" + e.getMessage());
        }
    }

    public Optional<List<Order>> getOrdersSorted(String field, String descCondition, Logger logger) throws CanNotMakeExecution{
        String sql;
        if (field.equals("status_D")){
            sql = " SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status" +
                    " FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id " +
                    "WHERE status= 'D' ";
        }else if ( field.equals("status_C")){
            sql = " SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status" +
                    " FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id " +
                    "WHERE status= 'C' ";
        }
        else if (field.equals("status_N")){
            sql = " SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status" +
                    " FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id " +
                    "WHERE status= 'N' ";

        }else if (field.equals("total_cost")){
            sql = " SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status, order_totals.total_cost" +
                    " FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id " +
                    "INNER JOIn (" +
                    " SELECT ob.order_id, SUM(b.price) AS total_cost FROM order_books AS ob INNER JOIN books AS b ON b.book_id = ob.book_id " +
                    " GROUP By ob.order_id" +
                    ") AS order_totals ON order_totals.order_id = o.order_id "+
                    "WHERE o.status= 'D' ORDER BY " + field + " " + descCondition;
        } else{
            sql = " SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status" +
                    " FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id " +
                    "WHERE o.status= 'D' ORDER BY " + field + " " + descCondition;
        }

        try (Statement pr = getConnection().createStatement()){


            ResultSet resultSet = pr.executeQuery(sql);
            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                if (field.equals("total_cost")){
                    orders.add(mapRow(resultSet, true, logger));
                }else {
                    orders.add(mapRow(resultSet, logger));
                }
            }
            return Optional.of(orders);
        }
        catch (SQLException e){
            logger.error("Проблема getOrdersSorted OrderIMpl: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при сортировки заказов " + e.getMessage());
        }
    }



    private Customer getCustomer(String name, String surname, String email){
        return new Customer(name, surname, email);
    }
    private OrderStatus getStatus(String status) throws CanNotMakeExecution {
        return switch (status){
            case "N" -> OrderStatus.NEW;
            case "D" -> OrderStatus.DONE;
            case "C" -> OrderStatus.CANCELLED;
            default -> throw new CanNotMakeExecution(status);
        };
    }

    private String getStatusChar(OrderStatus status){
        return switch (status){
            case NEW -> "N";
            case DONE -> "D";
            case CANCELLED -> "C";
        };
    }
}

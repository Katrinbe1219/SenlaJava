package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Book;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.model.types.OrderStatus;
import com.example.custom_annotations.Inject;

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
    public Optional<List<Order>> findAll() throws CanNotMakeExecution{
        String sql  =" SELECT o.order_id, c.name, c.surname, c.email, o.completion_date, o.status FROM orders AS o INNER JOIN customers AS c On c.customer_id = o.customer_id;";
        List<Order> items = new ArrayList<>();
        try (
                Statement st = getConnection().createStatement();

        ){
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                items.add(mapRow(rs));
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
                return Optional.of(items);
            }
            return Optional.empty();
        }
        catch (SQLException e) {
            throw new CanNotMakeExecution("Проблема при нахождении обьектов  " + e.getMessage());
        }
    }

    @Override
    protected Order mapRow(ResultSet resultSet) throws CanNotMakeExecution {
        //order_id | name | surname | email | completion_date | status
        Order order = new Order();
        try {

            order.setId(resultSet.getInt("order_id"));
            order.setCustomer(getCustomer(resultSet.getString("name"), resultSet.getString("surname"), resultSet.getString("email")));
            order.setCompletionDate(resultSet.getObject("completion_date", LocalDate.class));
            order.setStatus(getStatus(resultSet.getString("status")));
            return order;
        } catch (SQLException e) {
            throw  new CanNotMakeExecution("Проблема в mapRow Orders: " + e.getMessage());
        }
    }

    @Override
    protected Integer getId(Order order) throws CanNotMakeExecution {
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
            throw new CanNotMakeExecution("Проблема при получении id у order: "+ e.getMessage());
        }
    }

    @Override
    protected Order insert(Order order) throws CanNotMakeExecution {
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
    protected Order update(Order order) throws CanNotMakeExecution {
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
            throw new CanNotMakeExecution("Обновление заказа не было выполнено " + e.getMessage());
        }

    }

    public List<Order> getOrders(){
        Optional<List<Order>> orders_ = findAll();
        return orders_.orElse(null);
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
            case OrderStatus.NEW -> "N";
            case OrderStatus.DONE -> "D";
            case OrderStatus.CANCELLED -> "C";
        };
    }
}

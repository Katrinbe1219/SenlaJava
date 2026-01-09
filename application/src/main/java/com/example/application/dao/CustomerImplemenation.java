package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Customer;
import com.example.custom_applications.Inject;

import java.sql.*;

@Inject
public class CustomerImplemenation extends AbstractDao<Customer, Integer>{

    @Inject
    Connection connection;

    @Override
    protected Customer mapRow(ResultSet resultSet) throws CanNotMakeExecution {
        Customer customer = new Customer();
        try {
            customer.setCustomerId(resultSet.getInt("customer_id"));
            customer.setName(resultSet.getString("name"));
            customer.setEmail(resultSet.getString("email"));
            customer.setSurname(resultSet.getString("surname"));
            return customer;
        }catch (SQLException e) {
            throw new CanNotMakeExecution("Проблема с получением покупателя: "+ e.getMessage());
        }


    }

    @Override
    protected Integer getId(Customer customer) throws CanNotMakeExecution {

        String sql = "SELECT customer_id FROM customers WHERE name = ? AND  surname = ? AND email = ?";
        try (
                PreparedStatement pr =getConnection().prepareStatement(sql)
        ){
            pr.setString(1, customer.getName());
            pr.setString(2, customer.getSurname());
            pr.setString(3, customer.getEmail());
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                customer.setCustomerId(rs.getInt("customer_id"));
                return rs.getInt("customer_id");
            }
            return null;
        }
        catch (SQLException e) {
            throw new CanNotMakeExecution("Проблема с получением id покупателя: "+ e.getMessage());
        }
    }

    @Override
    protected Customer insert(Customer customer) throws CanNotMakeExecution {
        String sql = "INSERT INTO customers (name, surname, email) VALUES(?,?,?) ";
        try (PreparedStatement pr = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pr.setString(1,customer.getName());
            pr.setString(2,customer.getSurname());
            pr.setString(3,customer.getEmail());

            int affectedRows = pr.executeUpdate();
            if (affectedRows == 0) {
                throw new CanNotMakeExecution("Покупатель не был добавлен в бд. Проблемы на сервере");
            }
            ResultSet keys = pr.getGeneratedKeys();
            if (keys.next()) {
                customer.setCustomerId(keys.getInt(1));
                return customer;
            }

            throw new CanNotMakeExecution("Новых ключей не обнаружено. Пробелемы на сервере. Customer");
        }
        catch (SQLException e) {
            throw new CanNotMakeExecution("Проблема при добавлении пользователя: "+ e.getMessage());
        }
    }

    @Override
    protected String getTableName() throws CanNotMakeExecution {
        return "customers";
    }

    @Override
    protected Connection getConnection() throws CanNotMakeExecution {
        return this.connection;
    }

    @Override
    protected Customer update(Customer customer) throws CanNotMakeExecution {
        // в программе такой функции не наблюдается, поэтому будем считать, что изменяется пользователь по его customer_id
        String sql = "UPDATE customers SET name = ?, surname = ?, email = ? WHERE customer_id = ?";
        try (PreparedStatement pr = getConnection().prepareStatement(sql)){
            pr.setString(1,customer.getName());
            pr.setString(2,customer.getSurname());
            pr.setString(3,customer.getEmail());
            pr.setInt(4,customer.getCustomerId());

            int affectedRows = pr.executeUpdate();
            if (affectedRows == 0) {
                throw new CanNotMakeExecution("Обновление не сработало - пользователя. Проблема на  сервере");
            }

            return customer;
        }
        catch (SQLException e) {
            throw new CanNotMakeExecution("Проблема при обновлении пользователя: "+ e.getMessage());
        }
    }


}

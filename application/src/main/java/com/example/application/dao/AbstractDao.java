package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao <T,ID> implements  GenericDao<T,ID>{
    protected Connection connection;
    protected abstract T mapRow(ResultSet resultSet) throws CanNotMakeExecution;
    protected abstract ID getId(T t) throws CanNotMakeExecution;
    protected abstract T insert(T t) throws CanNotMakeExecution;
    protected abstract  String getTableName() throws CanNotMakeExecution;
    protected abstract Connection getConnection() throws CanNotMakeExecution;
    protected abstract T update(T t) throws CanNotMakeExecution;



    @Override
    //для  каждого свой прописать
    public Optional<List<T>> findAll() throws CanNotMakeExecution {
        String sql = "SELECT * FROM " + getTableName();
        List<T> items = new ArrayList<>();
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
    public Optional<T> findById(ID id) throws CanNotMakeExecution {
        String sql = "select * from " + getTableName() + " where id = ?";
        try (
            PreparedStatement pr = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pr.setObject(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();


        }catch (SQLException e) {
            throw new CanNotMakeExecution("Проблема при добавлении id " + e.getMessage());
        }
    }

    @Override
    public T save(T t) throws CanNotMakeExecution {
        if (getId(t) == null) {
            return insert(t);
        }else{
            return update(t);
        }
    }

    @Override
    public void delete(T t)  throws CanNotMakeExecution {
        String sql = "DELETE FROM " + getTableName() + "WHERE id = ?";
        try (
                PreparedStatement pr = getConnection().prepareStatement(sql)
        ){
            pr.setObject(1, getId(t));
            pr.executeUpdate();

        }
        catch (SQLException e) {
            throw  new CanNotMakeExecution("Проблема при удалении "+e.getMessage());
        }
    }


    @Override
    public long count() throws CanNotMakeExecution {
        String sql = "SELECT count(*) FROM " + getTableName();
        try(Statement st = getConnection().createStatement()

        ){
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }

            return 0L;


        }catch (SQLException e) {
            throw  new CanNotMakeExecution("Проблема  при подсчете обьектов  " + e.getMessage());
        }
    }

    @Override
    public T getById(ID id) throws CanNotMakeExecution{
        String sql = "SELECT * FROM " + getTableName() + " where id = ?";
        try (PreparedStatement pr = getConnection().prepareStatement(sql)){
            pr.setObject(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (Exception e) {
            throw new CanNotMakeExecution("Проблема с получением id " + e.getMessage());
        }
    }
}

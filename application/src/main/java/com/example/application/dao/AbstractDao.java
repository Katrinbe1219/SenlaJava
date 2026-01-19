package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao <T,ID> implements  GenericDao<T,ID, Logger>{
    private static final Logger log = LogManager.getLogger(AbstractDao.class);
    protected Connection connection;
    protected abstract T mapRow(ResultSet resultSet, Logger logger) throws CanNotMakeExecution;
    protected abstract ID getId(T t, Logger logger) throws CanNotMakeExecution;
    protected abstract T insert(T t, Logger logger) throws CanNotMakeExecution;
    protected abstract  String getTableName() throws CanNotMakeExecution;
    protected abstract Connection getConnection() throws CanNotMakeExecution;
    protected abstract T update(T t, Logger logger) throws CanNotMakeExecution;



    @Override
    //для  каждого свой прописать
    public Optional<List<T>> findAll(Logger logger) throws CanNotMakeExecution {
        String sql = "SELECT * FROM " + getTableName();
        List<T> items = new ArrayList<>();
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
            logger.error("Проблема при нахождении обьекта "  + e.getMessage());
            throw new CanNotMakeExecution("Проблема при нахождении обьектов  " + e.getMessage());
        }

    }

    @Override
    public Optional<T> findById(ID id, Logger logger) throws CanNotMakeExecution {
        String sql = "select * from " + getTableName() + " where id = ?";
        try (
            PreparedStatement pr = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pr.setObject(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs, logger));
            }
            return Optional.empty();


        }catch (SQLException e) {
            logger.error("Проблема при нахождении id " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при добавлении id " + e.getMessage());
        }
    }

    @Override
    public T save(T t, Logger logger) throws CanNotMakeExecution {
        if (getId(t, logger) == null) {
            return insert(t, logger);
        }else{
            return update(t, logger);
        }
    }

    @Override
    public void delete(T t, Logger logger)  throws CanNotMakeExecution {
        String sql = "DELETE FROM " + getTableName() + "WHERE id = ?";
        try (
                PreparedStatement pr = getConnection().prepareStatement(sql)
        ){
            pr.setObject(1, getId(t, logger));
            pr.executeUpdate();

        }
        catch (SQLException e) {
            logger.error("Проблема при удалении  " + e.getMessage());
            throw  new CanNotMakeExecution("Проблема при удалении "+e.getMessage());
        }
    }


    @Override
    public long count(Logger logger) throws CanNotMakeExecution {
        String sql = "SELECT count(*) FROM " + getTableName();
        try(Statement st = getConnection().createStatement()

        ){
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }

            return 0L;


        }catch (SQLException e) {
            logger.error("Проблема при подсчете обьектов " + e.getMessage());
            throw  new CanNotMakeExecution("Проблема  при подсчете обьектов  " + e.getMessage());
        }
    }

    @Override
    public T getById(ID id, Logger logger) throws CanNotMakeExecution{
        String sql = "SELECT * FROM " + getTableName() + " where id = ?";
        try (PreparedStatement pr = getConnection().prepareStatement(sql)){
            pr.setObject(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                return mapRow(rs, logger);
            }
            return null;
        } catch (Exception e) {
            logger.error("Проблема при получении id " + e.getMessage());
            throw new CanNotMakeExecution("Проблема с получением id " + e.getMessage());
        }
    }
}

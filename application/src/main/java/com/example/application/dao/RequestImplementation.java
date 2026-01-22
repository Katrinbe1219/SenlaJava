package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Request;
import com.example.application.model.RequestResult;
import com.example.custom_applications.Inject;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Inject
public class RequestImplementation extends AbstractDao<Request, Integer>{

    @Inject
    Connection connection;

    @Override
    protected Request mapRow(ResultSet resultSet, Logger logger) throws CanNotMakeExecution {
        Request request = new Request();
        try {
//            request.setBook(resultSet.getInt("book_id"));
//            request.setOrder(resultSet.getInt("order_id"));
            request.setId(resultSet.getInt("request_id"));
            return request;
        }
        catch (Exception e) {
            logger.error("Проблема mapROw RequestImpl: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при получении обьекта запроса: " + e.getMessage());
        }
    }

    @Override
    protected Integer getId(Request request, Logger logger) throws CanNotMakeExecution {
        return 0;
    }

    @Override
    protected Request insert(Request request, Logger logger) throws CanNotMakeExecution {
        return null;
    }

    @Override
    protected String getTableName() throws CanNotMakeExecution {
        return "requests";
    }

    @Override
    protected Connection getConnection() throws CanNotMakeExecution {
        return this.connection;
    }

    @Override
    protected Request update(Request request, Logger logger) throws CanNotMakeExecution {
        return null;
    }

    public List<RequestResult> getRequestsSorted(String field, String descCondition, Logger logger) throws CanNotMakeExecution {
        String sql = "SELECT b.title, count(r.request_id) AS amount FROM requests AS r INNER JOIN books AS b on b.book_id = r.book_id GROUP BY r.book_id,  b.title ORDER BY " +
                field + " " + descCondition;
        ArrayList<RequestResult> results = new ArrayList<>();

        try (Statement st = getConnection().createStatement()){
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
//                results.add(new RequestResult(
//                        rs.getString("title"),
//                        //rs.getInt("amount")
//                ));
            }
            return results;
        }
        catch (Exception e) {
            logger.error("Проблема в requestImplementation, getRequested Sorted: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при получении отсортированных заявок: " + e.getMessage());
        }
    }

    public List<Integer> insertMany(List<Integer> book_ids, int order_id, Logger logger) throws CanNotMakeExecution {
        String sql = "INSERT INTO requests(order_id, book_id) VALUES (?, ?)";

        try  (PreparedStatement pr = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            for (Integer book_id : book_ids) {
                pr.setInt(1, order_id);
                pr.setInt(2, book_id);
                pr.addBatch();
            }

            int[] affectedRows = pr.executeBatch();
            if (affectedRows.length == book_ids.size()) {
                return book_ids;
            }
            throw new CanNotMakeExecution("Не все запросы были добавлены в бд. Проблема на сервере");
        }
        catch (Exception e) {
            logger.error("Проблема в requestImplementation, insertMany: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при добавлении запросов: " + e.getMessage());
        }
    }

    public void deleteManyByBook(Integer book_id, Logger logger) throws CanNotMakeExecution {
        String sql = "DELETE FROM requests WHERE book_id = ?";
        try (PreparedStatement pr = getConnection().prepareStatement(sql)) {

            pr.setInt(1, book_id);

            int affectedRows = pr.executeUpdate();
            if (affectedRows != 0) {
                return;
            }
            throw  new CanNotMakeExecution("Не все запросы были удалены");
        }catch (Exception e) {
            logger.error("Проблема в requestImplementation, deleteManyByBook: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при удалении нескольких запросов: " +e.getMessage());
        }
    }

    public void deleteManyByOrder(Integer order_id, Logger logger) throws CanNotMakeExecution {
        String sql = "DELETE FROM requests WHERE order_id = ?";
        try (PreparedStatement pr = getConnection().prepareStatement(sql)) {

            pr.setInt(1, order_id);

            int affectedRows = pr.executeUpdate();
            if (affectedRows != 0) {
                return;
            }
            throw  new CanNotMakeExecution("Не все запросы были удалены");
        }catch (Exception e) {
            logger.error("Проблема в requestImplementation, deleteManyByOrder: " + e.getMessage());
            throw new CanNotMakeExecution("Проблема при удалении нескольких запросов: " +e.getMessage());
        }
    }

    public List<RequestResult> getRequestsByBook() throws CanNotMakeExecution{
        String sql = "SELECT b.title, r.request_id, r.order_id FROM requests AS r INNER JOIN books As b  ON b.book_id = r.book_id;";
        ArrayList<RequestResult> result = new ArrayList();
        try (Statement st = getConnection().createStatement()){

            ResultSet rs = st.executeQuery(sql);

            if (rs.next()){
//                result.add(new RequestResult(
//                        rs.getString("title"),
//                        rs.getInt("request_id")
//                ));

                while(rs.next()){
//                    result.add(new RequestResult(
//                            rs.getString("title"),
//                            rs.getInt("request_id")
//                    ));
                }
            }

            return result;
        }
        catch (SQLException e){
            throw  new CanNotMakeExecution("Проблема при получении запросов по книге: " + e.getMessage());
        }
    }
}

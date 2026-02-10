package com.example.application.hibernate;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.Request;
import com.example.application.model.RequestResult;

import com.example.custom_applications.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RequestHibImpl extends HibernateAbstractDao<Request, Integer, Logger> {
    public RequestHibImpl() {
        super(Request.class);
    }

    public void insertMany(List<Book> books, Order order, Logger logger, Session session) throws CanNotMakeExecution {

        if (session == null){
            session = HibernateUtils.getCurrentSession();
        }

        try{
            int batchSize = 10;
            int i;
            for (i = 0; i< books.size(); i++){
                session.persist(new Request(books.get(i), order));

                if ((i+1)%batchSize ==0){
                    session.flush();
                    session.clear();
                }

            }
            session.flush();

        } catch (Exception e) {
            logger.error("requestHibIMpl insertMany: " + e.getMessage());
            throw new CanNotMakeExecution("\nrequestHibIMpl insertMany: " + e.getMessage());
        }
    }

    public List<RequestResult> getRequestsSorted(String field, String descCondition, Logger logger) throws CanNotMakeExecution {

        try(Session session = HibernateUtils.getCurrentSession()) {

            String hql = "SELECT b.title, COUNT(r.id) AS amount " +
                    "FROM requests r INNER JOIN r.book b GROUP BY b.title ORDER BY "
                    + field + " " + descCondition;

            return session.createQuery(hql, RequestResult.class).getResultList();


        } catch (Exception e) {
            logger.error("RequestHibImpl getRequestSorted: " + e.getMessage());
            throw new CanNotMakeExecution("\nRequestHibImpl getRequestSorted:  " + e.getMessage());
        }
    }

    public void deleteManyByBook(Book book, Logger logger) throws  CanNotMakeExecution{
        Session session = HibernateUtils.getCurrentSession();
        try  {
            Transaction tx = session.beginTransaction();
            String hql= "DELETE FROM requests r WHERE r.book = :book";
            session.createMutationQuery(hql).setParameter("book", book).executeUpdate();

            tx.commit();

        }catch (Exception e){
            logger.error("RequestHibImpl deleteManyByBook: " + e.getMessage());
            throw new CanNotMakeExecution("\nRequestHibImpl deleteManyByBook: " + e.getMessage());
        }finally {
            session.close();
        }
    }

    public void deleteManyByOrder(Order order, Logger logger) throws  CanNotMakeExecution{
        Session session = HibernateUtils.getCurrentSession();
        try {
            Transaction tx = session.beginTransaction();
            String hql= "DELETE FROM requests r WHERE r.order = :order";
            session.createQuery(hql).setParameter("order", order).executeUpdate();
            tx.commit();
        }
        catch (Exception e){
            logger.error("RequestHibImpl deleteManyByOrder: " + e.getMessage());
            throw new CanNotMakeExecution("\nRequestHibImpl deleteManyByOrder: " + e.getMessage());
        }finally {
            session.close();
        }
    }

}

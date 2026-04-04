package com.example.application.hibernate;

import com.example.application.dto.ReceiveRequest;
import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.Request;
import com.example.application.model.RequestResult;

import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@DependsOn("liquibase")
public class RequestHibImpl extends HibernateAbstractDao<Request, Integer, Logger> {
    public RequestHibImpl() {
        super(Request.class);
    }


    @Transactional
    public List<ReceiveRequest> insertMany(List<Book> books, Order order, Logger logger) throws CanNotMakeExecution {

        Session session = getSessionFactory().getCurrentSession();
        List<ReceiveRequest> requests = new ArrayList<>();

        try{
            int batchSize = 10;
            int i;
            for (i = 0; i< books.size(); i++){
                ReceiveRequest request = new ReceiveRequest();
                request.setBookName(books.get(i).getTitle());
                requests.add(request);
                session.persist( new Request(books.get(i), order));

                if ((i+1)%batchSize ==0){
                    session.flush();
                    session.clear();
                }

            }

            return requests;


        } catch (Exception e) {
            logger.error("requestHibIMpl insertMany: " + e.getMessage());
            throw new CanNotMakeExecution("\nrequestHibIMpl insertMany: " + e.getMessage());
        }
    }


    @Transactional
    public List<RequestResult> getRequestsSorted(String field, String descCondition, Logger logger) throws CanNotMakeExecution {
        Session session = getSessionFactory().getCurrentSession();
        try {

            String hql = "SELECT b.title, COUNT(r.id) AS amount " +
                    "FROM requests r INNER JOIN r.book b GROUP BY b.title ORDER BY "
                    + field + " " + descCondition;

            return session.createQuery(hql, RequestResult.class).getResultList();


        } catch (Exception e) {
            logger.error("RequestHibImpl getRequestSorted: " + e.getMessage());
            throw new CanNotMakeExecution("\nRequestHibImpl getRequestSorted:  " + e.getMessage());
        }
    }

    @Transactional
    public void deleteManyByBook(Book book, Logger logger) throws  CanNotMakeExecution{
        Session session = getSessionFactory().getCurrentSession();
        try  {

            String hql= "DELETE FROM requests r WHERE r.book = :book";
            session.createMutationQuery(hql).setParameter("book", book).executeUpdate();



        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("RequestHibImpl deleteManyByBook: " + e.getMessage());
            throw new CanNotMakeExecution("\nRequestHibImpl deleteManyByBook: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteManyByOrder(Order order, Logger logger) throws  CanNotMakeExecution{
        Session session = getSessionFactory().getCurrentSession();
        try {

            String hql= "DELETE FROM requests r WHERE r.order = :order";
            session.createQuery(hql).setParameter("order", order).executeUpdate();

        }
        catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("RequestHibImpl deleteManyByOrder: " + e.getMessage());
            throw new CanNotMakeExecution("\nRequestHibImpl deleteManyByOrder: " + e.getMessage());
        }
    }

}

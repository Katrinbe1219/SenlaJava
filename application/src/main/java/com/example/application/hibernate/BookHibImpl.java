package com.example.application.hibernate;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Book;
import com.example.application.model.types.BookStatus;
import com.example.custom_applications.Inject;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Inject
public class BookHibImpl extends  HibernateAbstractDao<Book, Integer, Logger>{

    public BookHibImpl() {
        super(Book.class);
    }

    // изменения в книгах могут быть только при завозе, то есть передается наименование, нужен ее поиск и затем изменение статуса и даты завоза
    public Book save (Logger logger, String title)throws CanNotMakeExecution {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            // если такой книги не будет, merge создаст новую
            Book book = getBookByTitle(logger, title, session) ;

            if (book != null) {
                book.setStatus(BookStatus.IN_STOCK);
                Book updated = session.merge(book);
                tx.commit();
                return updated;
            }

           throw new CanNotMakeExecution("Книги не было найдено");

        }
        catch (Exception e){
            tx.rollback();
            logger.error("Проблема в BookHIbIMpl save : " + e.getMessage());
            throw new  CanNotMakeExecution("\nBookHIbIMpl save : " + e.getMessage());
        }finally {
            session.close();
        }

    }

    public Book getBookByTitle(Logger logger, String title, Session session)throws CanNotMakeExecution {
        if (session == null){
            session = HibernateUtils.getCurrentSession();
        }

        try {
            HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Book>  cquery= session.getCriteriaBuilder().createQuery(Book.class);
            JpaRoot<Book> root = cquery.from(Book.class);

            cquery.select(root).where(builder.equal(root.get("title"), title));
            Book result = session.createQuery(cquery).uniqueResult();
            System.out.println("result + " + result.getId());
            return result;

        } catch (Exception e){
            logger.error("Проблема в BookHIbIMpl getBookByTitle : " + e.getMessage());
            throw new  CanNotMakeExecution("\nBookHIbIMpl getBookByTitle :" + e.getMessage());
        }




    }

    public List<Book> getSortedBooks (String field, boolean descCondition, Logger logger){

        try (Session session = HibernateUtils.getCurrentSession();){
            HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Book> cq = builder.createQuery(Book.class);
            JpaRoot <Book> root = cq.from(Book.class);

            if (descCondition){
                cq.orderBy(builder.desc(root.get(field)));
            }else{
                cq.orderBy(builder.asc(root.get(field)));
            }

            root.fetch("author", JoinType.LEFT);

            return session.createQuery(cq).getResultList();

        }
        catch (Exception e){

            logger.error("Проблема в BookHIbIMpl getSortedBooks: " + e.getMessage());
            throw new  CanNotMakeExecution("\nBookHIbIMpl getSortedBooks : " + e.getMessage());
        }
    }

    public List<Book> getSortedBooks (String field, String status, Logger logger){
        Session session = HibernateUtils.getCurrentSession();
        try {
            HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Book> cq = builder.createQuery(Book.class);
            JpaRoot <Book> root = cq.from(Book.class);

            BookStatus bookStatus = getBookStatus(status);
           cq.where(builder.equal(root.get(field), bookStatus));
            root.fetch("author", JoinType.LEFT);

            return session.createQuery(cq).getResultList();

        }
        catch (Exception e){

            logger.error("Проблема в BookHIbIMpl getSortedBooks boolean : " + e.getMessage());
            throw new  CanNotMakeExecution("BookHIbIMpl getSortedBooks boolean : " + e.getMessage());
        }finally {
            session.close();
        }
    }

    public List<Book> getLongLiedBooks (Integer numberOfMonth, String field, boolean descCondition, Logger logger) throws CanNotMakeExecution {
        try(Session session = HibernateUtils.getCurrentSession();) {

            HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
            JpaCriteriaQuery <Book> cq = builder.createQuery(Book.class);
            JpaRoot<Book> root = cq.from(Book.class);

            LocalDate threshold = LocalDate.now().minusMonths(numberOfMonth);
            cq.select(root).where(builder.lessThan(root.get("lastPurchaseDate"), threshold));

            if (descCondition){
                cq.orderBy(builder.desc(root.get(field)));
            }else{
                cq.orderBy(builder.asc(root.get(field)));
            }
            return session.createQuery(cq).getResultList();
        }
        catch (Exception e){
            logger.error("Проблема в BookHIbIMpl getLongLied: " + e.getMessage());
            throw new  CanNotMakeExecution("\nBookHIbIMpl getLongLied : " + e.getMessage());
        }
    }

    public List<Book> updateBooksLastPurchase(List<Book> book_, Logger logger) throws CanNotMakeExecution {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Book> update = builder.createCriteriaUpdate(Book.class);
            Root<Book> root = update.from(Book.class);

            List<Integer> ids = getIds(book_);

            update.set(root.get("lastPurchaseDate"), LocalDate.now());
            update.where(root.get("id").in(ids));

            session.createQuery(update).executeUpdate();

            tx.commit();
            return book_;


        }
        catch (Exception e){
            tx.rollback();
            logger.error("Проблема в BookHIbIMpl updateBooksLastPurchase : " + e.getMessage());
            throw new  CanNotMakeExecution("\nBookHIbIMpl updateBooksLastPurchase : " + e.getMessage());
        }finally {
            session.close();
        }
    }

    private BookStatus getBookStatus(String type) throws CanNotMakeExecution {
        return switch (type){
            case "I" -> BookStatus.IN_STOCK;
            case "O" -> BookStatus.OUT_OF_STOCK;
            default -> throw new CanNotMakeExecution("\nBookHIbIMpl getBookStatus нет такого статуса ");
        };
    }

    private List<Integer> getIds(List<Book> books){
        List<Integer> ids = new ArrayList<Integer>();
        for (Book book : books){
            ids.add(book.getId());
        }
        return ids;
    }
}

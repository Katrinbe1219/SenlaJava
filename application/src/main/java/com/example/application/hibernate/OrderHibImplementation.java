package com.example.application.hibernate;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.Book;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.model.types.OrderStatus;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
@DependsOn("liquibase")
public class OrderHibImplementation extends HibernateAbstractDao<Order, Integer, Logger>{
    public OrderHibImplementation() {
        super(Order.class);
    }

    public void update(List<Order> order, Logger logger) throws CanNotMakeExecution {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        int batchSize = 10;
        int i =0;
        System.out.println("here");
        try{

            for (Order o : order) {
                session.update(o);
                if (++i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }
            session.flush();
            session.clear();


            tx.commit();


        }
        catch (Exception e) {
            logger.error("OrderHibIMpl update: " + e.getMessage());
            throw new CanNotMakeExecution("\nOrderHibIMpl update: " + e.getMessage());
        }finally {
            session.close();
        }
    }

    public void save(Order order, Logger logger, Session session, Transaction tx) throws CanNotMakeExecution{

        boolean foreign =true;

        if (session == null){
            foreign = false;
            session = HibernateUtils.getCurrentSession();
            tx = session.beginTransaction();
        }

        try {
            Customer customer = checkCustomer(order.getCustomer(), logger, session);
            if (customer == null){
                session.persist(order.getCustomer());
                session.flush();

            }else{
                order.setCustomer(customer);
            }


            session.persist(order);

            if (!foreign){
                // если транзакция была передана, значит коммит находится в исходной функции
                tx.commit();
                session.close();
            }


        }
        catch (Exception e) {
            tx.rollback();
            logger.error("OrderHibIMpl save: " + e.getMessage());
            throw new CanNotMakeExecution("OrderHibIMpl save: " + e.getMessage());
        }
    }

    private Customer checkCustomer(Customer customer, Logger logger, Session session) throws CanNotMakeExecution {
        String hql = "SELECT c FROM customers c WHERE c.name = :name AND c.surname = :surname AND c.email = :email";
        return session.createQuery(hql, Customer.class)
                .setParameter("surname", customer.getSurname())
                .setParameter("name", customer.getName())
                .setParameter("email", customer.getEmail())
                .uniqueResult();
    }

    public Order getId(Order order, Logger logger) throws CanNotMakeExecution {
        Session session = HibernateUtils.getCurrentSession();
        try {
            String hql = "SELECT o FROM orders o " +
                    "WHERE o.id = :id";

            return session.createQuery(hql, Order.class).setParameter("id", order.getId()).uniqueResult();
        }
        catch (Exception e) {
            logger.error("OrderHibIMpl getId: " + e.getMessage());
            throw new CanNotMakeExecution("\nOrderHibIMpl getId: " + e.getMessage());
        }finally {
            session.close();
        }
    }

    public List<Order> getOrdersSorted(OrderStatus status, Logger logger) throws CanNotMakeExecution {
        Session session = HibernateUtils.getCurrentSession();
        try {
            String hql = """
                SELECT DISTINCT o FROM orders o
                    LEFT JOIN FETCH o.books b
                    LEFT JOIN FETCH o.customer
                    LEFT JOIN FETCH b.author
                    WHERE o.status = :status
                    """;



            List<Order> orders = session.createQuery(hql, Order.class)
                    .setParameter("status", status)
                    .getResultList();

            return orders;
        }
        catch(Exception e){
            logger.error("OrderHibIMpl getOrdersSorted: " + e.getMessage());
            throw new CanNotMakeExecution("\nOrderHibIMpl getOrdersSorted: " + e.getMessage());
        }finally {
            session.close();
        }
    }

    public List<Order> getOrdersSorted(String field, boolean descCondition, Logger logger) throws CanNotMakeExecution {
        Session session = HibernateUtils.getCurrentSession();
        try {
            // рассуждения о totalCost - полной сумме заказа, которая до этого получалась через GROUP BY
            // чтобы не получить n+1
            // group by и fetch join не могут быть вместе
            // при выводе показываются книги, значит для каждого заказ они потом будут подгружаться
            // поэтому либо создается DTO, где будет чисто сумма, либо уж просто будет подсчет и сортировка если нужно по totalCost

            HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Order> cq = builder.createQuery(Order.class);
            Root<Order> root = cq.from(Order.class);

            if (descCondition){
                cq.select(root).orderBy(builder.desc(root.get(field)));
            }else {
                cq.select(root).orderBy(builder.asc(root.get(field)));
            }

            Fetch<Order, Book> booksFetch  = root.fetch("books", JoinType.LEFT);

            booksFetch.fetch("author", JoinType.LEFT);
            root.fetch("customer", JoinType.LEFT);


            return session.createQuery(cq).getResultList();

        }catch(Exception e){
            logger.error("OrderHibIMpl getOrdersSorted: " + e.getMessage());
            throw new CanNotMakeExecution("\nOrderHibIMpl getOrdersSorted: " + e.getMessage());
        } finally {
            session.close();
        }
    }


    public List<Order> getSortedDoneOrders(LocalDate start, LocalDate end, boolean descCondition, Logger logger)throws CanNotMakeExecution{
        Session session = HibernateUtils.getCurrentSession();
        try {

            String hql;
            if (descCondition){
                hql = """
            SELECT o FROM orders o
            LEFT JOIN FETCH o.books b
                    LEFT JOIN FETCH o.customer
                    LEFT JOIN FETCH b.author
            WHERE o.completionDate BETWEEN :start AND :end
            ORDER BY o.completionDate DESC
            """;

            }else {
                hql = """
            SELECT o FROM orders o
                    LEFT JOIN FETCH o.books b
                    LEFT JOIN FETCH o.customer
                    LEFT JOIN FETCH b.author
            WHERE o.completionDate BETWEEN :start AND :end
            ORDER BY o.completionDate ASC
            """;
            }

            return session.createQuery(hql, Order.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();

        }
        catch(Exception e){
            logger.error("OrderHibIMpl getSortedDoneOrders: " + e.getMessage());
            throw new CanNotMakeExecution("OrderHibIMpl getSortedDoneOrders: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public List<Order> getSortedDoneOrders(LocalDate start, LocalDate end,  Logger logger)throws CanNotMakeExecution{
        Session session = HibernateUtils.getCurrentSession();
        try {
            // Чтобы не получить N+1, totalCost рассчитывается в коде и сортировка по цене также произойдет в нем

            String hql =  """
            SELECT o FROM orders o
            LEFT JOIN FETCH o.books b
                    LEFT JOIN FETCH o.customer
                    LEFT JOIN FETCH b.author
            WHERE o.completionDate BETWEEN :start AND :end AND o.status = :status
            """;



            return session.createQuery(hql, Order.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .setParameter("status", OrderStatus.DONE)
                    .getResultList();

        }
        catch(Exception e){
            logger.error("OrderHibIMpl getSortedDoneOrders: " + e.getMessage());
            throw new CanNotMakeExecution("OrderHibIMpl getSortedDoneOrders: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public List<Order> getOrdersInDiapazon(LocalDate start, LocalDate end,  Logger logger) throws CanNotMakeExecution{
        // функция используется не для вывода, только для подсчета totalCost и amount, поэтому не подгружаю author, customer
        Session session = HibernateUtils.getCurrentSession();
        try {

            String hql =  """
            SELECT o FROM orders o
        LEFT JOIN FETCH o.books b
            WHERE o.completionDate BETWEEN :start AND :end
            """;



            return session.createQuery(hql, Order.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();

        }
        catch(Exception e){
            logger.error("OrderHibIMpl getOrdersInDiapazon: " + e.getMessage());
            throw new CanNotMakeExecution("OrderHibIMpl getOrdersInDiapazon: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public Order getById(int id, Logger logger) throws CanNotMakeExecution{

        Session session = HibernateUtils.getCurrentSession();try {

            String hql = "SELECT o FROM orders o " +
                    "LEFT JOIN FETCH o.books b " +
                    "LEFT JOIN FETCH b.author " +
                    "LEFT JOIN FETCH o.customer " +
                    "WHERE o.id = :id";

            return session.createQuery(hql, Order.class).setParameter("id", id).uniqueResult();
        }
        catch (Exception e) {
            logger.error("OrderHibIMpl getById: " + e.getMessage());
            throw new CanNotMakeExecution("OrderHibIMpl getById: " + e.getMessage());
        } finally {
            session.close();
        }
    }

}

package org.example.consumer_application.jdbc;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateImpl {
    private SessionFactory sessionFactory;

    public HibernateImpl(LocalSessionFactoryBean sessionFactory) {
        this.sessionFactory = sessionFactory.getObject();
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public void changeBalance(Account account) {
        getSession().update(account);

    }

    @Transactional
    public void insertTransaction(TransactionsTable table){
        getSession().persist(table);
    }

    @Transactional
    public Account getAccount(int id)throws Exception {
        try{
            Session session = getSession();
            String hql = "SELECT * FROM user_account WHERE id = ?";
            return session.createNativeQuery(hql, Account.class).setParameter(1, id).uniqueResult();
        } catch (Exception e) {
            throw new Exception("Ошибка при getAccount: " + e.getMessage());
        }
    }
}

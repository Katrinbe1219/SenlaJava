package com.example.application.hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import  org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    private static final SessionFactory sessionFactory;
    private static Session session;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    public static Session getCurrentSession () throws HibernateException{
        if (session != null && session.isOpen()) {
            return session;
        };
        session = sessionFactory.openSession();
        return session;
    }
}

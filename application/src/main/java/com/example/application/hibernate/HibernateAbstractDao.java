package com.example.application.hibernate;

import com.example.application.errors.CanNotMakeExecution;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.Serializable;
import java.util.List;


public class HibernateAbstractDao<T, ID extends Serializable, Logger> implements HibernateGenericDao<T,ID,Logger>{

    @Autowired
    private SessionFactory sessionFactory;

    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private  Class<T> entityClass;
    HibernateAbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void update(T entity, Logger logger) {

    }

    @Override
    public void delete(T entity, Logger logger) {

    }

    @Override
    public T findById(ID id, Logger logger) {
        return null;
    }

    @Override
    @Transactional
    public List<T> findAll(Logger logger) throws CanNotMakeExecution {
        try {
            Session session = getSessionFactory().getCurrentSession();
            HibernateCriteriaBuilder crBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<T> criteriaQuery =  crBuilder.createQuery(getEntityClass());
            JpaRoot<T> root = criteriaQuery.from(getEntityClass());

            criteriaQuery.select(root);
            return session.createQuery(criteriaQuery).getResultList();
        }
        catch (Exception e) {
            throw new CanNotMakeExecution("Проблема при findAll " + e.getMessage());
        }


    }

    @Override
    public void save(T entity, Logger logger) {

    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }
}

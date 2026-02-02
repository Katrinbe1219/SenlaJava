package com.example.application.hibernate;

import com.example.application.errors.CanNotMakeExecution;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;


import java.io.Serializable;
import java.util.List;

public class HibernateAbstractDao<T, ID extends Serializable, Logger> implements HibernateGenericDao<T,ID,Logger>{

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
    public List<T> findAll(Logger logger) throws CanNotMakeExecution {
        try {
            Session session = HibernateUtils.getCurrentSession();
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
        HibernateUtils.getCurrentSession().save(entity);
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }
}

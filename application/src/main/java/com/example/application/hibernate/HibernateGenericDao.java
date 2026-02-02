package com.example.application.hibernate;

import java.io.Serializable;
import java.util.List;

public interface HibernateGenericDao<T, ID extends Serializable, Logger> {
    void update(T entity, Logger logger);
    void delete(T entity, Logger logger);
    T findById(ID id, Logger logger);
    List<T> findAll(Logger logger);
    void save(T entity, Logger logger);
}

package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID, Logger> {
    Optional<List<T>> findAll(Logger logger) throws CanNotMakeExecution;
    Optional<T> findById(ID id, Logger logger) throws CanNotMakeExecution;

    T save(T t, Logger logger) throws CanNotMakeExecution;
    void delete(T t, Logger logger) throws CanNotMakeExecution;
    T getById(ID id, Logger logger) throws CanNotMakeExecution;

    long count(Logger logger) throws CanNotMakeExecution;

}

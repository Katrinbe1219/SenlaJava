package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {
    Optional<List<T>> findAll() throws CanNotMakeExecution;
    Optional<T> findById(ID id) throws CanNotMakeExecution;

    T save(T t) throws CanNotMakeExecution;
    void delete(T t) throws CanNotMakeExecution;
    T getById(ID id) throws CanNotMakeExecution;

    long count() throws CanNotMakeExecution;

}

package com.example.application.dao;

import com.example.application.errors.CanNotMakeExecution;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {
    public Optional<List<T>> findAll() throws CanNotMakeExecution;
    public Optional<T> findById(ID id) throws CanNotMakeExecution;

    public T save(T t) throws CanNotMakeExecution;
    public void delete(T t) throws CanNotMakeExecution;
    public T getById(ID id) throws CanNotMakeExecution;

    long count() throws CanNotMakeExecution;

}

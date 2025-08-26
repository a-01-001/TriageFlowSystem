package com.triageflow.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {
    Optional<T> findById(int id);
    List<T> findAll();
    T save(T entity);
    T update(T entity);
    void delete(int id);
}
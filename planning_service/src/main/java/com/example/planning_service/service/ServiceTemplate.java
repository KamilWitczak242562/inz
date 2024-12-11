package com.example.planning_service.service;

import java.util.List;

public interface ServiceTemplate<T> {
    T create(T entity);

    boolean delete(Long id);

    T update(Long id, T entity);

    T getById(Long id);

    List<T> getAll();
}

package com.example.planning_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ServiceTemplate<T> {
    T create(T entity);

    boolean delete(Long id);

    T update(Long id, T entity);

    T getById(Long id);

    List<T> getAll();

    List<Map<String, Object>> getHistory(LocalDateTime startTime, LocalDateTime endTime, String revisionType);

}

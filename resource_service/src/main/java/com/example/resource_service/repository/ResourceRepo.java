package com.example.resource_service.repository;

import com.example.resource_service.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ResourceRepo extends JpaRepository<Resource, Long> {
    List<Resource> findAllByNameIn(Set<String> names);
}

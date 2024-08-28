package com.example.resource_service.repository;

import com.example.resource_service.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRepo extends JpaRepository<Step, Long> {
}

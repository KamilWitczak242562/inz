package com.example.resource_service.repository;

import com.example.resource_service.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepo extends JpaRepository<Program, Long> {
}

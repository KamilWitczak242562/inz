package com.example.machine_service.repository;

import com.example.machine_service.model.Dryer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DryerRepository extends JpaRepository<Dryer, Long> {
}

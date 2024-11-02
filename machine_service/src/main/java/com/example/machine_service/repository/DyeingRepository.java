package com.example.machine_service.repository;

import com.example.machine_service.model.Dyeing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DyeingRepository extends JpaRepository<Dyeing, Long> {
}

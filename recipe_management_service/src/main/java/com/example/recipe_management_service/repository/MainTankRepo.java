package com.example.recipe_management_service.repository;

import com.example.recipe_management_service.model.MainTank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainTankRepo extends JpaRepository<MainTank, Long> {
}

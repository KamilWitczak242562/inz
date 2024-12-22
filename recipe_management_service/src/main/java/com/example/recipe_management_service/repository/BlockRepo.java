package com.example.recipe_management_service.repository;

import com.example.recipe_management_service.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepo extends JpaRepository<Block, Long> {
}

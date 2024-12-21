package com.example.recipe_management_service.repository;

import com.example.recipe_management_service.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface BlockRepo extends JpaRepository<Block, Long> {
}

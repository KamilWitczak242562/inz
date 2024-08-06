package com.example.resource_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.resource_service.model.Notification;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
}

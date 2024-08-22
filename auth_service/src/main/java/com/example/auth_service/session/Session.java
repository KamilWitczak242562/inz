package com.example.auth_service.session;

import com.example.auth_service.model.User;
import lombok.Data;

import java.time.LocalTime;

@Data
public class Session {
    private User user;
    private LocalTime localTime;

    public Session(User user) {
        this.user = user;
        this.localTime = LocalTime.now();
    }
}

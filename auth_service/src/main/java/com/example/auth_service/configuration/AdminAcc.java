package com.example.auth_service.configuration;

import com.example.auth_service.model.User;
import com.example.auth_service.model.enums.Role;
import com.example.auth_service.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AdminAcc {

    private final UserRepo userRepo;

    @Bean
    public ApplicationRunner initAdminAccount() {
        return args -> {
            String adminEmail = "admin@example.com";
            if (userRepo.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setLastName("Account");
                admin.setEmail(adminEmail);
                admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
                admin.setRole(Role.ADMIN);

                userRepo.save(admin);
                System.out.println("Admin account created: " + adminEmail);
            } else {
                System.out.println("Admin account already exists: " + adminEmail);
            }
        };
    }
}


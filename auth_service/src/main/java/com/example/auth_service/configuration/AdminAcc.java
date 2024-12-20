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
            String adminEmail = "admin";
            String userEmail = "user";
            if (userRepo.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setFirstName("Janusz");
                admin.setLastName("Kowalski");
                admin.setEmail(adminEmail);
                admin.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));
                admin.setRole(Role.ADMIN);

                userRepo.save(admin);

                User user = new User();
                user.setFirstName("Jan");
                user.setLastName("Nowak");
                user.setEmail(userEmail);
                user.setPassword(BCrypt.hashpw("user", BCrypt.gensalt()));
                user.setRole(Role.USER);

                userRepo.save(admin);

                System.out.println("Admin account created: " + adminEmail);
                System.out.println("User account created: " + userEmail);
            } else {
                System.out.println("Admin account already exists: " + adminEmail);
                System.out.println("User account already exists: " + userEmail);
            }
        };
    }
}


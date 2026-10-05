package com.jc.professional_challenge_api.config;

import com.jc.professional_challenge_api.entities.Role;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Creates the parent admin account on startup if it does not exist yet.
// From that account the client's own account can be promoted to ADMIN.
@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    @Value("${app.admin.name}")
    private String name;

    @Value("${app.admin.last-name}")
    private String lastName;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // With a persistent database this avoids creating the account twice.
        if (userRepository.existsByEmail(email)) return;

        User admin = new User();
        admin.setName(name);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password)); //never plain text
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }
}

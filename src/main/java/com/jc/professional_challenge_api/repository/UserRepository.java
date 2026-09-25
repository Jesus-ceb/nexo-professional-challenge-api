package com.jc.professional_challenge_api.repository;

import com.jc.professional_challenge_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //Check for the existence of an email to avoid duplicates.
    boolean existsByEmail(String email);

    //Search for and retrieve the User object from the database.
    Optional<User> findByEmail(String email);
}

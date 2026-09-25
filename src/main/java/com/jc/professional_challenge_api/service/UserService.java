package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.UserRegisterRequest;
import com.jc.professional_challenge_api.controller.dto.UserResponse;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(UserRegisterRequest request){

        //email validation that prevents duplicates
        if (userRepository.existsByEmail(request.email())){
            throw new IllegalStateException("Ya existe una cuenta con este correo");
        }

        //This is where the actual entity to be stored in the database is constructed.
        User user = new User();
        user.setName(request.name());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        //It takes the password and converts it into an irreversible hash.
        user.setPassword(passwordEncoder.encode(request.password())); //never plain text

        //Saves the user to the database.
        User saved = userRepository.save(user);

        //The response is constructed using only the fields we want to expose to the client.
        return new UserResponse(saved.getId(), saved.getName(), saved.getLastName(), saved.getEmail());
    }
}

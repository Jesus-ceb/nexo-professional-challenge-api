package com.jc.professional_challenge_api.controller;

import com.jc.professional_challenge_api.controller.dto.UserRegisterRequest;
import com.jc.professional_challenge_api.controller.dto.UserResponse;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Registers a new user, validates the received data, and returns the created user with HTTP status 201 (Created).
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody  UserRegisterRequest request){
        try {
            UserResponse created = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }

    // Returns the personal data of the logged-in user (requires "Authorization: Bearer <token>").
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user){
        return userService.toResponse(user);
    }


}

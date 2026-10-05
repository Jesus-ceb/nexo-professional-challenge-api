package com.jc.professional_challenge_api.controller;

import com.jc.professional_challenge_api.controller.dto.LoginRequest;
import com.jc.professional_challenge_api.controller.dto.LoginResponse;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.service.JwtService;
import com.jc.professional_challenge_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // Validates email + password and returns a JWT plus the user's public data.
    // Wrong credentials throw BadCredentialsException, handled by GlobalExceptionHandler (401).
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user.getUsername());

        return new LoginResponse(token, userService.toResponse(user));
    }
}

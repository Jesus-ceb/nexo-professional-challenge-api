package com.jc.professional_challenge_api.controller;

import com.jc.professional_challenge_api.controller.dto.UserRegisterRequest;
import com.jc.professional_challenge_api.controller.dto.UserResponse;
import com.jc.professional_challenge_api.controller.dto.UserRoleRequest;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    // Sends the registration confirmation email again to the logged-in user. 429 if asked again too soon.
    @PostMapping("/me/resend-confirmation")
    public ResponseEntity<Map<String, String>> resendConfirmation(@AuthenticationPrincipal User user){
        userService.resendConfirmation(user);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("message", "Te reenviamos el correo de confirmación"));
    }

    // Lists all registered users with their role (admin only, see SecurityConfig).
    @GetMapping
    public List<UserResponse> findAll(){
        return userService.findAll();
    }

    // Grants or removes the ADMIN role (admin only). 409 if it would remove the parent or own admin role.
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody UserRoleRequest request,
                                        @AuthenticationPrincipal User currentUser){
        try {
            return ResponseEntity.ok(userService.updateRole(id, request.role(), currentUser));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


}

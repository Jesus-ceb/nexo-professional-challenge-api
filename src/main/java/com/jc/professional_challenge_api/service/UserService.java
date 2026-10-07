package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.UserRegisterRequest;
import com.jc.professional_challenge_api.controller.dto.UserResponse;
import com.jc.professional_challenge_api.entities.Role;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.exception.ResendTooSoonException;
import com.jc.professional_challenge_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    @Value("${app.admin.email}")
    private String parentAdminEmail;

    @Value("${app.mail.resend-cooldown-seconds:60}")
    private long resendCooldownSeconds = 60;

    //Last time a confirmation email was sent to each user (in memory, so a restart resets it).
    private final Map<Long, Instant> lastConfirmationSent = new ConcurrentHashMap<>();

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public UserResponse register(UserRegisterRequest request){

        //email validation that prevents duplicates
        if (userRepository.existsByEmail(request.email())){
            throw new IllegalStateException("Este correo ya tiene una cuenta existente");
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

        //Confirmation email right after registering (sent in the background).
        sendConfirmation(saved);

        return toResponse(saved);
    }

    //Sends the confirmation email again, at most once per cooldown so the endpoint can't be used to spam.
    public void resendConfirmation(User user) {
        Instant last = lastConfirmationSent.get(user.getId());
        if (last != null) {
            long elapsed = Duration.between(last, Instant.now()).toSeconds();
            if (elapsed < resendCooldownSeconds) {
                throw new ResendTooSoonException(resendCooldownSeconds - elapsed);
            }
        }
        sendConfirmation(user);
    }

    private void sendConfirmation(User user) {
        lastConfirmationSent.put(user.getId(), Instant.now());
        emailService.sendRegistrationConfirmation(user);
    }

    //Used by Spring Security during login and by the JWT filter to load the user by email.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con el correo: " + email));
    }

    //The response is constructed using only the fields we want to expose to the client (never the password).
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getLastName(), user.getEmail(), user.getRole());
    }

    //Returns every registered user for the admin customers table.
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    //Grants or removes the ADMIN role. The parent account and the admin's own account can't lose it,
    //so the site is never left without an administrator.
    @Transactional
    public UserResponse updateRole(Long id, Role role, User currentUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario con id: " + id));

        if (role != Role.ADMIN) {
            if (user.getEmail().equals(parentAdminEmail)) {
                throw new IllegalStateException("La cuenta principal no puede perder el rol de administrador");
            }
            if (user.getId().equals(currentUser.getId())) {
                throw new IllegalStateException("No puedes quitarte tu propio rol de administrador");
            }
        }

        user.setRole(role);
        return toResponse(userRepository.save(user));
    }
}

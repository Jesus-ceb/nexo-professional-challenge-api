package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.UserRegisterRequest;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.exception.ResendTooSoonException;
import com.jc.professional_challenge_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Lionel");
        user.setLastName("Messi");
        user.setEmail("lionel@falso.com");
    }

    @Test
    void register_sendsConfirmationEmail() {
        when(userRepository.existsByEmail("lionel@falso.com")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(new UserRegisterRequest("Lionel", "Messi", "lionel@falso.com", "12345678"));

        verify(emailService).sendRegistrationConfirmation(user);
    }

    @Test
    void register_duplicatedEmail_doesNotSendEmail() {
        when(userRepository.existsByEmail("lionel@falso.com")).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                userService.register(new UserRegisterRequest("Lionel", "Messi", "lionel@falso.com", "12345678")));

        verifyNoInteractions(emailService);
    }

    @Test
    void resendConfirmation_firstTime_sendsEmail() {
        userService.resendConfirmation(user);

        verify(emailService).sendRegistrationConfirmation(user);
    }

    @Test
    void resendConfirmation_tooSoon_throwsAndDoesNotSendAgain() {
        userService.resendConfirmation(user);

        ResendTooSoonException ex = assertThrows(ResendTooSoonException.class,
                () -> userService.resendConfirmation(user));

        assertTrue(ex.getSecondsLeft() > 0);
        verify(emailService, times(1)).sendRegistrationConfirmation(user);
    }
}

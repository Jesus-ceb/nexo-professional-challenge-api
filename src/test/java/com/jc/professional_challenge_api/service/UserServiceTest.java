package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.UserRegisterRequest;
import com.jc.professional_challenge_api.controller.dto.UserResponse;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.exception.DuplicateResourceException;
import com.jc.professional_challenge_api.exception.ResendTooSoonException;
import com.jc.professional_challenge_api.exception.ResourceNotFoundException;
import com.jc.professional_challenge_api.repository.ProductRepository;
import com.jc.professional_challenge_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

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

    @Mock
    private ProductRepository productRepository;

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
        when(userRepository.existsByEmailIgnoreCase("lionel@falso.com")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(new UserRegisterRequest("Lionel", "Messi", "lionel@falso.com", "12345678"));

        verify(emailService).sendRegistrationConfirmation(user);
    }

    @Test
    void register_duplicatedEmail_doesNotSendEmail() {
        when(userRepository.existsByEmailIgnoreCase("lionel@falso.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                userService.register(new UserRegisterRequest("Lionel", "Messi", "lionel@falso.com", "12345678")));

        verifyNoInteractions(emailService);
    }

    @Test
    void register_storesTheEmailNormalized() {
        when(userRepository.existsByEmailIgnoreCase("juan@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("hash");
        // the database assigns the id
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        UserResponse created = userService.register(new UserRegisterRequest("Juan", "Perez", " Juan@Mail.com ", "12345678"));

        assertEquals("juan@mail.com", created.email());
    }

    @Test
    void register_sameEmailWithOtherCase_isDuplicated() {
        // juan@mail.com already exists
        when(userRepository.existsByEmailIgnoreCase("juan@mail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                userService.register(new UserRegisterRequest("Juan", "Perez", "Juan@mail.com", "12345678")));
        verify(userRepository, never()).save(any(User.class));
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

    // ------------------ favorites -----------------------------

    @Test
    void addFavorite_addsTheProductOnlyOnce() {
        Product product = new Product();
        product.setId(7L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        userService.addFavorite(user, 7L);
        userService.addFavorite(user, 7L);

        assertEquals(List.of(7L), userService.getFavoriteIds(user));
    }

    @Test
    void addFavorite_unknownProduct_throwsNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.addFavorite(user, 99L));
    }

    @Test
    void removeFavorite_removesTheProduct() {
        Product product = new Product();
        product.setId(7L);
        user.getFavorites().add(product);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.removeFavorite(user, 7L);

        assertTrue(user.getFavorites().isEmpty());
    }
}

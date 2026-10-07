package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.entities.User;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;
    private User user;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));

        emailService = new EmailService(mailSender, "no-reply@nexo.com", "http://localhost:5173");

        user = new User();
        user.setId(1L);
        user.setName("Lionel");
        user.setLastName("Messi");
        user.setEmail("lionel@falso.com");
    }

    @Test
    void buildRegistrationHtml_includesNameEmailAndLoginLink() {
        String html = emailService.buildRegistrationHtml(user);

        assertTrue(html.contains("¡Hola Lionel!"));
        assertTrue(html.contains("Lionel Messi"));
        assertTrue(html.contains("lionel@falso.com"));
        assertTrue(html.contains("http://localhost:5173/login?email=lionel@falso.com"));
        assertFalse(html.contains("{{"));
    }

    @Test
    void buildRegistrationHtml_escapesUserData() {
        user.setName("<script>");

        String html = emailService.buildRegistrationHtml(user);

        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    @Test
    void sendRegistrationConfirmation_sendsTheEmail() throws Exception {
        emailService.sendRegistrationConfirmation(user);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendRegistrationConfirmation_smtpFailure_doesNotPropagate() {
        doThrow(new MailSendException("SMTP caído")).when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.sendRegistrationConfirmation(user));
    }
}

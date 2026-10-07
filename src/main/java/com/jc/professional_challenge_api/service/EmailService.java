package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String REGISTRATION_SUBJECT = "¡Bienvenido a Nexo! Confirmación de registro";

    private final JavaMailSender mailSender;
    private final String from;
    private final String frontendUrl;
    private final String registrationTemplate;

    @Autowired
    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.from}") String from,
                        @Value("${app.frontend.url}") String frontendUrl) {
        this.mailSender = mailSender;
        this.from = from;
        this.frontendUrl = frontendUrl;
        this.registrationTemplate = loadTemplate("templates/registration-email.html");
    }

    //Runs in the background (@Async): registration never waits for the SMTP server and never fails because of it.
    @Async
    public void sendRegistrationConfirmation(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject(REGISTRATION_SUBJECT);
            helper.setText(buildRegistrationHtml(user), true);

            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            log.error("No se pudo enviar el correo de confirmación a {}", user.getEmail(), e);
        }
    }

    //Fills the template; user data is escaped so a name can't inject HTML into the email.
    String buildRegistrationHtml(User user) {
        String fullName = (user.getName() + " " + user.getLastName()).trim();
        String loginUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/login")
                .queryParam("email", user.getEmail())
                .encode()
                .toUriString();

        return registrationTemplate
                .replace("{{name}}", HtmlUtils.htmlEscape(user.getName()))
                .replace("{{fullName}}", HtmlUtils.htmlEscape(fullName))
                .replace("{{email}}", HtmlUtils.htmlEscape(user.getEmail()))
                .replace("{{loginUrl}}", HtmlUtils.htmlEscape(loginUrl));
    }

    private static String loadTemplate(String path) {
        try {
            return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se encontró la plantilla de correo " + path, e);
        }
    }
}

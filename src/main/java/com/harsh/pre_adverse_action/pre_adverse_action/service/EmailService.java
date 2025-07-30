package com.harsh.pre_adverse_action.pre_adverse_action.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(SimpleMailMessage message) {
        log.info("Preparing to send pre-adverse action email: {}", message);
        try {
            this.mailSender.send(message);
            log.info("Pre-adverse action email sent successfully to {}", message);
        } catch (Exception e) {
            log.error("Failed to send email to {}", message, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

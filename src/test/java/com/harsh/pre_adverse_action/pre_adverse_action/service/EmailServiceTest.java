package com.harsh.pre_adverse_action.pre_adverse_action.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private AutoCloseable openMocks;


    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (openMocks != null) {
            openMocks.close();
        }
    }

    @Test
    void sendEmail_success() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("recipient@example.com");
        message.setSubject("Test Subject");
        message.setText("Test body");

        assertDoesNotThrow(() -> emailService.sendEmail(message));

        verify(mailSender, times(1)).send(message);
    }

    @Test
    void sendEmail_failure_throwsRuntimeException() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("recipient@example.com");
        message.setSubject("Test Subject");
        message.setText("Test body");

        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(message);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> emailService.sendEmail(message));
        assertEquals("Failed to send email", ex.getMessage());

        verify(mailSender, times(1)).send(message);
    }
}

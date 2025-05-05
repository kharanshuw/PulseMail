package com.example.demo;

import com.example.demo.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailSenderTest {

    private EmailService emailService;

    @Autowired
    public EmailSenderTest(EmailService emailService) {
        this.emailService = emailService;
    }

    @Test
    void emailsendtest() {
        emailService.sendEmail("pratikwanare1999@gmail.com",
                "testing application",
                "this is email from springboot",
                "kharanshuw@gmail.com");
    }
}

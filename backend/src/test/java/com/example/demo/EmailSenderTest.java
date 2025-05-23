package com.example.demo;

import com.example.demo.model.EmailMessage;
import com.example.demo.service.EmailService;
import com.example.demo.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
public class EmailSenderTest {

    private EmailService emailService;

    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

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

    @Test
    public void emailsendwithhtmltest() {

        String htmlString = "<h1 style=\"color: red;\">\r\n" + //
                "        this is email testing for html content\r\n" + //
                "    </h1>";
        emailService.sendEmailWithHtml("pratikwanare1999@gmail.com", "testing email with html", htmlString,
                "kharanshuw@gmail.com");
    }

    @Test
    public void emailwithfile() {

        File file = new File(
                "D:\\spring boot project vs-code\\demo\\src\\main\\resources\\static\\Screenshot 2025-05-06 220242.png");
        emailService.sendEmailWithFile("pratikwanare1999@gmail.com", "testing email with file", "testing file send",
                file, "kharanshuw@gmail.com");
    }

    @Test
    public void emailwithinputstram() {
        try {

            File file = new File(
                    "D:\\spring boot project vs-code\\demo\\src\\main\\resources\\static\\Screenshot 2025-05-06 220242.png");

            InputStream inputStream = new FileInputStream(file);
            emailService.sendEmailWithFile("pratikwanare1999@gmail.com ",
                    "testing inputStream",

                    "testing inputStream",
                    inputStream,
                    "kharanshuw@gmail.com ",
                    "Screenshot");

        } catch (Exception e) {
            logger.error("error occured in emailwithinputstram test");
        }

    }


    @Test
    public void testGetInboxMessages() {
        // ✅ Call service method
        List<EmailMessage> messages = emailService.getInboxMessages();

        // ✅ Assertions
        assertNotNull(messages, "Inbox messages list should not be null");
        assertTrue(messages.size() > 0, "There should be at least one email in the inbox");

        // ✅ Verify first message properties (adjust assertions based on expected behavior)
        EmailMessage email = messages.get(0);
        assertNotNull(email.getSubject(), "Email subject should not be null");
        assertNotNull(email.getContent(), "Email content should not be null");
        assertNotNull(email.getFiles(), "Attachments list should not be null");
    }

}

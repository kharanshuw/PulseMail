package com.example.demo;

import com.example.demo.helper.EmailMessages;
import com.example.demo.service.Emailservice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class EmailTest {
    
    private Emailservice emailservice;

    @Autowired
    public EmailTest(Emailservice emailservice) {
        this.emailservice = emailservice;
    }

    @Test
    public void testreciveingemailsubject()
    {
        emailservice.getInboxSubject();
    }


    @Test
    public void testGetInboxMessages() {
        // When
        List<EmailMessages> messages = emailservice.getInboxMessages();

        // Then
        // Since our method returns an empty list if the inbox is empty,
        // we only need to ensure that the list is not null.
        assertNotNull(messages, "The returned messages list should not be null");

        // Optional: Print out the received messages for further inspection.
        messages.forEach(emailMessage ->
                System.out.println("Email subject: " + emailMessage.getSubject())
        );
    }
}

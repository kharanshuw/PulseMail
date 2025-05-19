package com.example.demo;

import com.example.demo.helper.EmailMessages;
import com.example.demo.service.Emailservice;
import com.example.demo.serviceimpl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EmailTest {
    private Logger logger = LoggerFactory.getLogger(EmailTest.class);


    private Emailservice emailservice;

    @Autowired
    public EmailTest(Emailservice emailservice) {
        this.emailservice = emailservice;
    }

    @Test
    public void testreciveingemailsubject() {
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


    @Test
    void testGetEmailCount_Failure() throws MessagingException {
        logger.info("testing testGetEmailCount_Failure() ");
        Map<String ,Integer > emailCount = emailservice.getEmailCount();
        
        emailCount.forEach(
                (k,v) -> logger.info("Key: {}, Value: {}", k, v)
        );
    }
    
    @Test
    public void testsearchEmails()
    {
        emailservice.searchEmails("orders@adda247.com");
    }

}

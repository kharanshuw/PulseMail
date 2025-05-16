package com.example.demo;

import com.example.demo.service.Emailservice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailTest {
    
    private Emailservice emailservice;

    @Autowired
    public EmailTest(Emailservice emailservice) {
        this.emailservice = emailservice;
    }

    @Test
    public void reciveingemail()
    {
        emailservice.getInboxMessages();
    }
}

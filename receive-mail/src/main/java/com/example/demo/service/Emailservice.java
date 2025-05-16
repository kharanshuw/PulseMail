package com.example.demo.service;

import com.example.demo.helper.EmailMessages;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface Emailservice {

    List<EmailMessages> getInboxMessages();

}

package com.example.demo.service;

import com.example.demo.helper.EmailMessages;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface Emailservice {

    List<EmailMessages> getInboxSubject();

    public List<EmailMessages> getInboxMessages();


    public Map<String, Integer> getEmailCount();

    public void searchEmails(String from);

}

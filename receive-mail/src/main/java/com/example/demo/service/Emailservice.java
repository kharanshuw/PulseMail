package com.example.demo.service;

import com.example.demo.dto.responsedto.EmailDto;
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

    public void deleteEmailsFromSender(String senderEmails);

    public List<EmailDto> fetchEmails(String sender, String subject, Boolean unread, int limit, String sinceDate);

}

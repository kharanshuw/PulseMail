package com.example.demo.service;

import java.io.File;

public interface EmailService {

    public void sendEmail(String to, String subjecString, String messageString,String fromString);

    public void sendEmail(String to[], String subject, String messageString,String fromString);

    public void sendEmailWithHtml(String to, String subjecString, String htmlcontentString);

    public void sendEmailWithFile(String to, String subjecString, String messageString, File file);

}

package com.example.demo.service;

import java.io.File;
import java.io.InputStream;

public interface EmailService {

    public void sendEmail(String to, String subjecString, String messageString, String fromString);

    public void sendEmail(String to[], String subject, String messageString, String fromString);

    public void sendEmailWithHtml(String to, String subjecString, String htmlcontentString, String fromString);

    public void sendEmailWithFile(String to, String subjecString, String messageString, File file, String fromString);

    public void sendEmailWithFile(String to, String subjecString, String messageString, InputStream inputStream,
            String fromString, String filenameString);
}

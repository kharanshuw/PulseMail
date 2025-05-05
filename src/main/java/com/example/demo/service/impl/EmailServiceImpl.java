package com.example.demo.service.impl;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender javaMailSender;

    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends an email using the provided recipient address, subject, and message
     * body.
     *
     * @param to            The recipient email address.
     * @param subjectString The email subject.
     * @param messageString The email body content.
     */
    public void sendEmail(String to, String subjectString, String messageString, String fromString) {

        logger.info("Preparing to send email to: {}", to);

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subjectString);
            simpleMailMessage.setText(messageString);
            simpleMailMessage.setFrom("kharanshuw@gmail.com");

            javaMailSender.send(simpleMailMessage);
            logger.info("Email successfully sent to: {}", to);
        } catch (MailException e) {
            logger.error("Email failed due to mail server issue: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid email parameters provided: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Sends an email to one or multiple recipients.
     *
     * @param to            Array of recipient email addresses.
     * @param subject       Subject of the email.
     * @param messageString Content of the email.
     * @param fromString    Sender's email address.
     * @throws IllegalArgumentException If any parameter is invalid (empty or
     *                                  malformed).
     * @throws MailException            If there is an issue with email delivery.
     * @throws Throwable                If an unexpected error occurs.
     */
    @Override
    public void sendEmail(String[] to, String subject, String messageString, String fromString) {

        try {

            logger.info("Starting email sending process...");

            // ✅ Validate recipient list
            if (to == null || to.length == 0) {
                throw new IllegalArgumentException("Recipient email addresses cannot be empty");
            }

            // ✅ Validate recipient email format

            for (String email : to) {
                if (!email.matches("^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,6}$")) {
                    throw new IllegalArgumentException("Invalid email format: " + email);
                }
            }
            // ✅ Validate email subject

            if (subject == null || subject.isBlank()) {
                throw new IllegalArgumentException("Email subject cannot be empty");
            }

            // ✅ Validate email message body

            if (messageString == null || messageString.isBlank()) {
                throw new IllegalArgumentException("Email body cannot be empty");
            }

            // ✅ Validate sender's email

            if (fromString == null || fromString.isEmpty()) {
                throw new IllegalArgumentException("Sender email cannot be empty");
            }

            logger.info("Preparing email with sender: {}, subject: {}", fromString, subject);

            // ✅ Create the email message

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setTo(to);

            simpleMailMessage.setSubject(subject);

            simpleMailMessage.setFrom(fromString);

            simpleMailMessage.setText(messageString);

            // ✅ Send the email
            javaMailSender.send(simpleMailMessage);

            logger.info("Email successfully sent to {}", Arrays.toString(to));

        } catch (IllegalArgumentException e) {

            logger.warn("Invalid email address format detected: {}", e.getMessage());
        } catch (MailException e) {

            logger.error("Email delivery failed due to mail server issue: {}", e.getMessage());
        }

        catch (Exception e) {

            logger.error("Failed to send email to {}: {}", Arrays.toString(to), e.getMessage());
        }

    }

    @Override
    public void sendEmailWithHtml(String to, String subjecString, String htmlcontentString) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEmailWithHtml'");
    }

    @Override
    public void sendEmailWithFile(String to, String subjecString, String messageString, File file) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEmailWithFile'");
    }

}

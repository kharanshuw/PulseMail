package com.example.demo.service.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.demo.service.EmailService;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

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

    /**
     * Sends an HTML-formatted email to a recipient.
     *
     * @param to                Recipient email address.
     * @param subject           Subject of the email.
     * @param htmlcontentString HTML content of the email body.
     * @param fromString        Sender email address.
     * @throws IllegalArgumentException If any parameter is empty or null.
     * @throws MessagingException       If there is an issue constructing the email.
     * @throws MailException            If there is an SMTP-related issue during
     *                                  sending.
     */
    @Override
    public void sendEmailWithHtml(String to, String subject, String htmlcontentString, String fromString) {

        try {

            logger.info("Starting the processto send an HTML email...");

            // ✅ Validate input parameters to prevent sending incomplete emails.
            if (to == null || to.isBlank()) {
                throw new IllegalArgumentException("Recipient email address cannot be empty.");
            }

            if (subject == null || subject.isBlank()) {
                throw new IllegalArgumentException("Email subject cannot be empty.");
            }

            if (htmlcontentString == null || htmlcontentString.isBlank()) {
                throw new IllegalArgumentException("Email body cannot be empty.");
            }

            if (fromString == null || fromString.isBlank()) {
                throw new IllegalArgumentException("Sender email cannot be empty.");
            }
            logger.info("Sending email from: {} to: {} with subject: {}", fromString, to, subject);

            // ✅ Create an HTML email message using MimeMessage

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            // ✅ Set email details

            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setFrom(fromString);
            messageHelper.setText(htmlcontentString, true);

            // ✅ Send the email using JavaMailSender

            javaMailSender.send(message);

            logger.info("Email successfully sent to {}", to);

        } catch (MessagingException e) {
            logger.error("Failed to construct HTML email: {}", e.getMessage(), e);
        } catch (MailException e) {
            logger.error("Email delivery failed due to SMTP issue: {}", e.getMessage(), e);
        }

        catch (Exception e) {
            logger.error("Unexpected error while sending HTML email to {}: {}", to, e.getMessage(), e);

        }

    }

    /**
     * Sends an email with a file attachment.
     *
     * @param to            Recipient email address.
     * @param subjecString  Subject of the email.
     * @param messageString Email body text.
     * @param file          Attachment file.
     * @param fromString    Sender's email address.
     * @throws IllegalArgumentException If any required parameter is missing or
     *                                  invalid.
     * @throws MessagingException       If an issue occurs while constructing the
     *                                  email.
     * @throws MailException            If the email fails to send due to an SMTP
     *                                  issue.
     */
    @Override
    public void sendEmailWithFile(String to, String subjecString, String messageString, File file, String fromString) {
        try {

            logger.info("Starting email send process with attachment...");

            // ✅ Validate Inputs Using Stream API

            if (to == null || to.isBlank()) {

                logger.warn("Validation failed: to parameters cannot be empty.");

                throw new IllegalArgumentException("Recipient email address cannot be empty.");
            }

            if (subjecString == null || subjecString.isBlank()) {

                logger.warn("Validation failed: Attachment subjecString does not exist.");

                throw new IllegalArgumentException("Email subject cannot be empty.");
            }

            if (messageString == null || messageString.isBlank()) {

                logger.warn("Validation failed: Attachment messageString does not exist.");

                throw new IllegalArgumentException("Email body cannot be empty.");
            }

            if (fromString == null || fromString.isBlank()) {

                logger.warn("Validation failed: Attachment fromString does not exist.");
                throw new IllegalArgumentException("Sender email cannot be empty.");
            }

            if (file == null || !file.exists()) {
                logger.warn("Validation failed: Attachment file does not exist.");

                throw new IllegalArgumentException("Attachment file does not exist.");
            }

            // ✅ Create an email message

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            logger.info("Preparing to send email from: {} to: {} with subject: {} and file: {}",
                    fromString, to, subjecString, file.getName());

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // ✅ Set email properties

            mimeMessageHelper.setFrom(fromString);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(messageString);

            mimeMessageHelper.setSubject(subjecString);

            mimeMessageHelper.addAttachment(file.getName(), file);

            logger.info("Email content prepared successfully. Proceeding with sending the email...");

            // ✅ Send the email

            javaMailSender.send(mimeMessage);

            logger.info("Email with attachment successfully sent to {}", to);

        } catch (MessagingException e) {
            logger.error("Failed to construct email with attachment: {}", e.getMessage(), e);
        } catch (MailException e) {
            logger.error("Email delivery failed due to SMTP issue: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending email with file to {}: {}", to, e.getMessage(), e);
        }

    }

    /**
     * Sends an email with a file attachment using JavaMailSender.
     *
     * This method constructs an email message, validates parameters,
     * and attaches a file from an InputStream before sending.
     *
     * @param to             Recipient email address.
     * @param subjecString   Subject of the email.
     * @param messageString  Email body content.
     * @param inputStream    InputStream of the file to be attached.
     * @param fromString     Sender's email address.
     * @param filenameString Name of the attached file.
     * @throws IllegalArgumentException If any required parameter is null or empty.
     * @throws MessagingException       If email construction fails.
     * @throws MailException            If email delivery fails due to an SMTP
     *                                  issue.
     */
    public void sendEmailWithFile(String to, String subjecString, String messageString, InputStream inputStream,
            String fromString, String filenameString) {

        try {
            logger.info("Starting email send process with attachment.");

            // ✅ Validate Inputs

            if (to == null || to.isBlank()) {

                logger.warn("Validation failed: to parameters cannot be empty.");

                throw new IllegalArgumentException("Recipient email address cannot be empty.");
            }

            if (subjecString == null || subjecString.isBlank()) {

                logger.warn("Validation failed: Attachment subjecString does not exist.");

                throw new IllegalArgumentException("Email subject cannot be empty.");
            }

            if (messageString == null || messageString.isBlank()) {

                logger.warn("Validation failed: Attachment messageString does not exist.");

                throw new IllegalArgumentException("Email body cannot be empty.");
            }

            if (fromString == null || fromString.isBlank()) {

                logger.warn("Validation failed: Attachment fromString does not exist.");
                throw new IllegalArgumentException("Sender email cannot be empty.");
            }
            // ✅ Ensure InputStream is not null

            if (inputStream == null) {
                logger.warn("Validation failed: InputStream is null, cannot attach file.");
                throw new IllegalArgumentException("Attachment input stream cannot be null.");
            }

            logger.info(
                    "Preparing to send email from: " + fromString + " to: " + to + " with subject: " + subjecString +
                            " and attachment: " + filenameString);

            // ✅ Create an email message

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // ✅ Set email properties

            mimeMessageHelper.setFrom(fromString);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(messageString);

            mimeMessageHelper.setSubject(subjecString);

            // ✅ Try-With-Resources for InputStream (Ensures Proper Resource Cleanup)
            try (InputStream stream = inputStream) {
                DataSource dataSource = new ByteArrayDataSource(stream, "application/octet-stream");
                mimeMessageHelper.addAttachment(filenameString, dataSource);
            }

            logger.info("Email content prepared successfully. Proceeding with sending the email...");

            // ✅ Send the email

            javaMailSender.send(mimeMessage);

            logger.info("Email with attachment successfully sent to {}", to);

        } catch (MessagingException e) {
            logger.error("Failed to construct email with attachment: {}", e.getMessage(), e);
        } catch (MailException e) {
            logger.error("Email delivery failed due to SMTP issue: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending email with file to {}: {}", to, e.getMessage(), e);
        }

    }

    
}
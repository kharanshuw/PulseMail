package com.example.demo.service.impl;

import com.example.demo.model.EmailMessage;
import com.example.demo.service.EmailService;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender javaMailSender;

    @Value("${mail.store.protocol}")
    private String protocol;

    @Value("${mail.imaps.host}")
    private String host;

    @Value("${mail.imaps.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

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
        } catch (Exception e) {

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
        } catch (Exception e) {
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
     * <p>
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
            mimeMessageHelper.setText(messageString, true);

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

    /**
     * Fetches messages from the INBOX folder of the user's email account.
     * Ensures proper resource management and robust exception handling.
     * 
     * @return List of EmailMessage objects containing subject, content, and
     *         attachments.
     */
    public List<EmailMessage> getInboxMessages() {

        Store store = null;
        Folder folder = null;
        try {

            // ✅ Initialize email session properties

            Properties properties = new Properties();

            properties.setProperty("mail.store.protocol", protocol);

            properties.setProperty("mail.imaps.host", host);

            properties.setProperty("mail.imaps.port", port);

            Session session = Session.getDefaultInstance(properties);

            // ✅ Connect to email store

            store = session.getStore();

            store.connect(username, password);

            logger.info("Successfully connected to mail store.");

            // ✅ Open INBOX folder in read-only mode

            folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);

            logger.info("Inbox folder opened successfully.");

            List<EmailMessage> list = new ArrayList<>();

            // ✅ Fetch messages

            jakarta.mail.Message[] messages = folder.getMessages();

            /**
             *
             * for (jakarta.mail.Message message : messages) {
             *
             * String content = getContentFromEmailMessage(message);
             *
             * List<String> files = getFilesFromEmailMessage(message);
             *
             * EmailMessage emailMessage = new EmailMessage();
             * emailMessage.setSubject(message.getSubject());
             * emailMessage.setFiles(files);
             *
             * emailMessage.setContent(content);
             *
             * list.add(emailMessage);
             *
             * }
             */

            // 🔹 Processing the last email only (for testing)

            if (messages.length > 0) {
                // Message message1 = messages[messages.length - 1];

                // logger.info("Processing the latest email with subject: " + message1.getSubject());

                // // ✅ Extract content and attachments

                // String content = getContentFromEmailMessage(message1);
                // List<String> files = getFilesFromEmailMessage(message1);

                // // ✅ Create EmailMessage object

                // EmailMessage emailMessage = new EmailMessage();
                // emailMessage.setSubject(message1.getSubject());
                // emailMessage.setFiles(files);
                // emailMessage.setContent(content);

                // list.add(emailMessage);


                for (jakarta.mail.Message message : messages) {
             
                    String content = getContentFromEmailMessage(message);
                    
                     List<String> files = getFilesFromEmailMessage(message);
                    
                     EmailMessage emailMessage = new EmailMessage();
                     emailMessage.setSubject(message.getSubject());
                     emailMessage.setFiles(files);
                   
                     emailMessage.setContent(content);
                    
                     list.add(emailMessage);
                    }
            } else {
                logger.warn("No emails found in the inbox!");

            }





            return list;

        } catch (Exception e) {
            logger.error("Exception occurred inside getInboxMessages", e);

            throw new RuntimeException("Exception occured inside getInboxMessages");
        } finally {

            // ✅ Ensure folder and store are properly closed to prevent memory leaks

            if (folder != null && folder.isOpen()) {
                try {
                    folder.close(false); // Ensures the folder is properly closed

                    logger.info("Inbox folder closed successfully.");

                } catch (MessagingException e) {
                    logger.error(
                            "Failed to close folder in getInboxMessages. Possible issue: disconnection or lock state.",
                            e);

                }
            }
            if (store != null) {
                try {
                    store.close(); // Ensures the store connection is released

                    logger.info("Mail store closed successfully.");

                } catch (MessagingException e) {
                    logger.error(
                            "Failed to close store in getInboxMessages. Possible issue: disconnection or session termination.",
                            e);

                }
            }
        }

    }

    /**
     * Extracts attachments from an email and saves them in a local directory.
     * Ensures unique file names to prevent overwrites.
     * 
     * @param message The email message to extract attachments from.
     * @return A list of saved file paths.
     */
    private List<String> getFilesFromEmailMessage(Message message) {

        try {
            List<String> files = new ArrayList<>();

            if (message.isMimeType("multipart/*")) {

                Multipart multipart = (Multipart) message.getContent();

                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);

                    InputStream inputStream = bodyPart.getInputStream();

                    // ✅ Validate and process attachments

                    if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) && bodyPart.getFileName() != null) {

                        String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_"
                                + bodyPart.getFileName();
                        File file = new File("src/main/resources/email/" + uniqueFilename);

                        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        files.add(file.getAbsolutePath());
                        logger.info("Saved attachment: " + uniqueFilename);
                    }

                }
            }

            return files;
        } catch (Exception e) {
            logger.error("Error occurred inside getFilesFromEmailMessage", e);

            throw new RuntimeException("error occured inside getFilesFromEmailMessage");
        }

    }

    /**
     * Extracts content from an email, prioritizing HTML format over plain text.
     * 
     * @param message The email message to extract content from.
     * @return The extracted email content or null if unavailable.
     */
    private String getContentFromEmailMessage(jakarta.mail.Message message) {

        try {

            logger.info("Extracting content from email with subject: " + message.getSubject());

            if (message.isMimeType("text/html")) {
                return (String) message.getContent();
            }
            if (message.isMimeType("text/plain")) {
                return (String) message.getContent();
            }
            if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();

                String htmlContent = null;
                String textContent = null;

                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);

                    if (bodyPart.isMimeType("text/html")) {
                        htmlContent = (String) bodyPart.getContent();
                    }
                    if (bodyPart.isMimeType("text/plain")) {
                        textContent = (String) bodyPart.getContent();
                    }
                }

                return htmlContent != null ? htmlContent : textContent;
            }

        } catch (Exception e) {
            // TODO: handle exception
            logger.error("exception occured in getContentFromEmailMessage", e);

            throw new RuntimeException("exception occured in getContentFromEmailMessage");
        }
        logger.warn("No content found in email.");
        return null;

    }

}
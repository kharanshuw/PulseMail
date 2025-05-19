package com.example.demo.serviceimpl;

import com.example.demo.exception.EmailServiceException;
import com.example.demo.helper.EmailMessages;
import com.example.demo.service.Emailservice;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.OrTerm;
import jakarta.mail.search.SearchTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class EmailServiceImpl implements Emailservice {

    private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

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

    /**
     * Retrieves email messages from the user's Gmail inbox.
     * <p>
     * This method connects to the Gmail server using IMAP, accesses the INBOX folder,
     * fetches email subjects, and logs them. It ensures proper resource management by
     * closing connections in a `finally` block.
     * </p>
     *
     * @return A list of {@link EmailMessages} containing the subjects of fetched emails.
     * Returns an empty list if the inbox is empty.
     * @throws EmailFetchException     If an error occurs while retrieving emails.
     * @throws MessagingException      If there is an issue with the email protocol.
     * @throws NoSuchProviderException If the email provider is incorrect.
     */
    public List<EmailMessages> getInboxSubject() {

        Folder folder = null;

        Store store = null;

        List<EmailMessages> emailMessages = new ArrayList<>();


        logger.info("Starting email retrieval process..."); // ✅ Initial logging


        try {

            // ✅ Step 1: Setting up properties

            logger.info("Configuring email properties...");


            Properties properties = new Properties();

            properties.setProperty("mail.store.protocol", protocol);

            properties.setProperty("mail.imaps.host", host);

            properties.setProperty("mail.imaps.port", port);

            // ✅ Step 2: Establishing Session & Store connection


            logger.info("Initializing email session...");


            Session session = Session.getDefaultInstance(properties);

            store = session.getStore();

            logger.info("Connecting to email store...");


            store.connect(username, password);

            // ✅ Step 3: Accessing INBOX folder


            logger.info("Accessing INBOX folder...");


            folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);

            logger.info("INBOX folder opened in READ_ONLY mode.");

            logger.info("Retrieving email messages...");


            // ✅ Step 4: Fetching email messages

            jakarta.mail.Message messages[] = folder.getMessages();

            logger.info("Total emails retrieved: " + messages.length);

            // ✅ Step 5: Handling empty inbox scenario

            if (messages.length == 0) {
                logger.warn("Inbox is empty!");
                return emailMessages;

            }


            boolean fetchSingleEmail = true;  // ✅ Make it configurable


            // ✅ Step 6: Processing and logging email subjects

            for (jakarta.mail.Message message : messages) {
                String subject = message.getSubject();
                logger.info("Processing email - Subject: " + subject);
                EmailMessages emailMessages1 = new EmailMessages();
                emailMessages1.setSubject(subject);
                emailMessages.add(emailMessages1);
                if (fetchSingleEmail) break;  // ✅ Controlled behavior
            }


            // ✅ Step 7: Logging latest email subject

            Message latestMessage = messages[messages.length - 1];


            logger.info("✔ Latest Email Retrieved | Subject: {}", latestMessage.getSubject());


        } catch (NoSuchProviderException e) {
            logger.error("Email provider error: " + e.getMessage(), e);
            //  throw new RuntimeException(e);
        } catch (MessagingException e) {

            logger.error("Messaging error occurred: " + e.getMessage(), e);

            // throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Unexpected error during email retrieval: " + e.getMessage(), e);

            //    throw new RuntimeException(e);
        } finally {

            // ✅ Step 8: Cleaning up resources

            logger.info("Closing email resources...");
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(false); // ✅ Close folder safely
                    logger.info("INBOX folder closed.");

                }
                if (store != null) {
                    store.close(); // ✅ Close store safely

                    logger.info("Email store connection closed.");

                }
            } catch (MessagingException e) {
                logger.error("Error closing resources: " + e.getMessage());
            }
        }

        logger.info("Email retrieval process completed.");
        return emailMessages;


    }


    /**
     * Retrieves email messages from the INBOX folder.
     * <p>
     * This method configures the email properties from externalized configuration,
     * connects to the email store, fetches messages, and processes each message by
     * extracting the subject, sender addresses, and content.
     * </p>
     *
     * @return a list of EmailMessages objects representing the retrieved emails.
     */
    public List<EmailMessages> getInboxMessages() {
        Folder folder = null;

        Store store = null;

        List<EmailMessages> emailMessages = new ArrayList<>();


        logger.info("Starting email retrieval process..."); // ✅ Initial logging


        try {


            //✅ step 1 and 2 Modularized session and store connection
            Session session = createEmailSession();
            store = connectToEmailStore(session);


            // ✅ Step 3: Accessing INBOX folder


            logger.info("Accessing INBOX folder...");


            folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);

            logger.info("INBOX folder opened in READ_ONLY mode.");

            logger.info("Retrieving email messages...");


            // ✅ Step 4: Fetching email messages

            jakarta.mail.Message messages[] = folder.getMessages();

            logger.info("Total emails retrieved: {}", messages.length);

            // ✅ Step 5: Handling empty inbox scenario

            if (messages.length == 0) {
                logger.warn("Inbox is empty!");
                return emailMessages;

            }


            boolean fetchSingleEmail = true;  // ✅ Make it configurable


            // ✅ Step 6: Processing and logging email subjects

            for (jakarta.mail.Message message : messages) {


                try {
                    EmailMessages processedMessage = processMessage(message);
                    emailMessages.add(processedMessage);
                } catch (Exception ex) {
                    logger.error("Error processing a message: {}", ex.getMessage(), ex);
                }
                if (fetchSingleEmail) {
                    break;  // Controlled behavior: process only one email
                }
            }


        } catch (NoSuchProviderException e) {
            logger.error("Email provider error: {}", e.getMessage(), e);

        } catch (MessagingException e) {

            logger.error("Messaging error occurred: " + e.getMessage(), e);


        } catch (Exception e) {
            logger.error("Unexpected error during email retrieval: " + e.getMessage(), e);


        } finally {

            // ✅ Step 8: Cleaning up resources

            logger.info("Closing email resources...");
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(false); // ✅ Close folder safely
                    logger.info("INBOX folder closed.");

                }
                if (store != null) {
                    store.close(); // ✅ Close store safely

                    logger.info("Email store connection closed.");

                }
            } catch (MessagingException e) {
                logger.error("Error closing resources: " + e.getMessage());
            }
        }

        logger.info("Email retrieval process completed.");
        return emailMessages;

    }


    /**
     * Processes a single email message by extracting its subject, sender addresses, and content.
     *
     * @param message the email message to process
     * @return an EmailMessages object with processed data
     */
    private EmailMessages processMessage(jakarta.mail.Message message) {
        EmailMessages emailMessages = new EmailMessages();
        try {
            // Process subject
            String subject = message.getSubject();
            // Using null-check first to avoid NullPointerException
            if (subject == null || subject.isEmpty() || subject.isBlank()) {
                logger.warn("Subject is empty");
                emailMessages.setSubject(null);
            } else {
                emailMessages.setSubject(subject);
            }


            // Process "from" addresses
            List<String> fromList = new ArrayList<>();

            Address[] addresses = message.getFrom();

            if (addresses != null) {
                for (Address address : addresses) {
                    fromList.add(address.toString());
                }
            } else {
                logger.warn("Email 'from' addresses are null.");
            }


            if (fromList.isEmpty()) {
                logger.warn("From list is empty");
                emailMessages.setFrom(null);
            } else {
                emailMessages.setFrom(fromList);
            }

            // Process content
            Object content = message.getContent();
            if (content == null) {
                logger.warn("Content is empty");
                emailMessages.setContent(null);
            } else {
                emailMessages.setContent(content.toString());
            }
        } catch (MessagingException e) {
            logger.error("Messaging exception occurred in processMessage: {}", e.getMessage(), e);

            throw new EmailServiceException("Failed to retrieve inbox messages" + e);
        } catch (IOException e) {
            logger.error("IOException exception occured in processmessage: {} ", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Exception exception occured in processmessage : {} ", e.getMessage(), e);
        }

        return emailMessages;
    }

    /**
     * Creates an email session based on externalized properties.
     */
    private Session createEmailSession() {
        // Log the start of properties configuration

        logger.info("Configuring email properties...");


        // Set email properties required for connection

        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", protocol);
        properties.setProperty("mail.imaps.host", host);
        properties.setProperty("mail.imaps.port", port);


        // Create a session based on the properties.
        // Using Session.getDefaultInstance() ensures the system-wide configuration is applied.

        Session session = Session.getDefaultInstance(properties);
        logger.info("Email session created.");
        return session;
    }


    /**
     * Connects to the email store using the provided session and credentials.
     *
     * @param session the email session
     * @return the connected email store
     * @throws MessagingException if connection fails
     */
    private Store connectToEmailStore(Session session) throws MessagingException {

        // Get the Store object from the session. The store will be used for accessing mail folders.

        Store store = session.getStore();
        logger.info("Connecting to email store...");

        // Connect to the email store using the username and password.
        // This step may throw MessagingException if the credentials or server are incorrect.
        store.connect(username, password);
        logger.info("Connected to email store.");
        return store;
    }

    /**
     * Retrieves the total and unread email count from the INBOX folder.
     * <p>
     * This method establishes an email session, connects to the email store,
     * retrieves email count statistics, logs necessary details, and ensures
     * proper resource cleanup to prevent memory leaks.
     * </p>
     *
     * <b>Usage:</b>
     * <pre>
     * Map<String, Integer> emailStats = getEmailCount();
     * int totalEmails = emailStats.get("totalMessages");
     * int unreadEmails = emailStats.get("unreadMessages");
     * </pre>
     *
     * @return A map containing the total email count (key: "totalMessages")
     * and unread email count (key: "unreadMessages").
     * @throws MessagingException If there is an issue connecting to the email store
     *                            or retrieving the message count.
     * @throws Exception          If any unexpected error occurs during email retrieval.
     */
    public Map<String, Integer> getEmailCount() {
        Folder folder = null;

        Store store = null;

        Map<String, Integer> emailStats = new HashMap<>();

        logger.info("Starting email count retrieval...");

        try {
            // ✅ Step 1: Initialize session and connect to the email store

            logger.info("Initializing email session and store connection...");

            Session session = createEmailSession();
            store = connectToEmailStore(session);

            // ✅ Step 2: Access the INBOX folder

            logger.info("Accessing INBOX folder...");


            folder = store.getFolder("INBOX");


            folder.open(Folder.READ_ONLY);

            logger.info("INBOX folder opened in READ_ONLY mode.");

            // ✅ Step 3: Retrieve email counts

            logger.info("Fetching total and unread email counts...");


            int messageCount = folder.getMessageCount();


            int unreadMessageCount = folder.getUnreadMessageCount();

            // ✅ Step 4: Log the fetched results for debugging

            logger.info("Email count retrieval successful: Total: {}, Unread: {}", messageCount, unreadMessageCount);

            // ✅ Step 5: Store counts in the map for returning

            emailStats.put("totalMessages", messageCount);
            emailStats.put("unreadMessages", unreadMessageCount);


        } catch (MessagingException e) {

            // ✅ Log messaging-specific errors (e.g., connection, retrieval issues)

            logger.error("Messaging exception occurred while fetching email count: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving email count: {}", e.getMessage(), e);
        } finally {
            logger.info("Cleaning up email resources...");
            try {

                // ✅ Ensure the folder is safely closed

                if (folder != null && folder.isOpen()) {
                    folder.close(false);
                    logger.info("INBOX folder closed successfully.");
                }

                // ✅ Close the store connection to free up resources

                if (store != null) {
                    store.close();

                    logger.info("Email store connection closed successfully.");

                }
            } catch (MessagingException e) {
                logger.error("Error closing email resources: {}", e.getMessage(), e);
            }
        }
        // ✅ Log warning if returning an empty map due to errors

        if (emailStats.isEmpty()) {

            // ✅ Handle errors that may occur while closing resources

            logger.warn("Returning empty email stats due to an exception.");

        }

        logger.info("Email count retrieval process completed.");

        // ✅ Return the final email statistics

        return emailStats;
    }


    public void searchEmails2(String senderemail) {

        Folder folder = null;

        Store store = null;
        try {
            Session session = createEmailSession();
            store = connectToEmailStore(session);

            folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);

            if (senderemail == null || senderemail.isEmpty()) {
                logger.warn("Skipping email search due to empty sender email.");
                return;
            }


            SearchTerm searchTerm = new OrTerm(new FromStringTerm(senderemail), new FromStringTerm(senderemail.toLowerCase()));


            Message[] messages = folder.search(searchTerm);

            // ✅ Log how many emails were found before processing them
            logger.info("Total emails found for '{}': {}", senderemail, messages.length);


            if (messages.length == 0) {
                logger.warn("No emails found for '{}'.", senderemail);
                return;
            }


            for (Message message : messages) {
                logger.info("Subject: {}", message.getSubject());


                Object content = message.getContent();
                if (content instanceof String) {
                    logger.info("content: {}", content);
                } else if (content instanceof Multipart) {
                    logger.info("content: [Multipart message detected]");
                } else {
                    logger.info("content: [Unsupported content type]");
                }
            }


        } catch (AddressException e) {
            logger.error("Invalid email address format: {}", e.getMessage(), e);
        } catch (MessagingException e) {
            logger.error("Messaging exception occurred while searching emails: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Error reading email content: {}", e.getMessage(), e);
        } finally {
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(false);  // Closes folder safely
                    logger.info("INBOX folder closed.");
                }
                if (store != null) {
                    store.close();  // Closes store safely
                    logger.info("Email store connection closed.");
                }
            } catch (MessagingException e) {
                logger.error("Error closing email resources: {}", e.getMessage(), e);
            }
        }


    }


    public void searchEmails3(String senderemail) {

        Folder folder = null;

        Store store = null;
        try {
            Session session = createEmailSession();
            store = connectToEmailStore(session);

            folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);

            if (senderemail == null || senderemail.isEmpty()) {
                logger.error("Email search failed: Sender email is empty or null.");
                return;
            }

            SearchTerm searchTerm = new OrTerm(new FromStringTerm(senderemail), new FromStringTerm(senderemail.toLowerCase()));


            Message[] messages = folder.search(searchTerm);

            // ✅ Log how many emails were found before processing them
            logger.info("Total emails found for '{}': {}", senderemail, messages.length);


            if (messages.length == 0) {
                logger.warn("No emails found for '{}'. Ensure the sender address is correct or check the inbox.", senderemail);
                return;
            }


            for (Message message : messages) {
                logger.info("Subject: {}", message.getSubject());


                Object content = message.getContent();
                if (content instanceof String) {
                    logger.info("content: {}", content);
                } else if (content instanceof Multipart) {

                    String extractedContent = "[No readable content]";

                    Multipart multipart = (Multipart) content;

                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        if (bodyPart.isMimeType("text/plain")) {
                            extractedContent = bodyPart.getContent().toString();
                            break;
                        } else if (bodyPart.isMimeType("text/html") && extractedContent.equals("[No readable content]")) {
                            extractedContent = bodyPart.getContent().toString();
                        }

                        // ✅ Detect inline images inside HTML emails
                        if (bodyPart.isMimeType("text/html") && bodyPart.getContent().toString().contains("<img")) {
                            logger.info("Email contains embedded images.");
                        }
                    }

                    logger.info("content: {}", extractedContent);
                } else {
                    logger.info("content: [Unsupported content type]");
                }
            }


        } catch (AddressException e) {
            logger.error("Invalid email address format: {}", e.getMessage(), e);
        } catch (MessagingException e) {
            logger.error("Messaging exception occurred while searching emails: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Error reading email content: {}", e.getMessage(), e);
        } finally {
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(false);  // Closes folder safely
                    logger.info("INBOX folder closed.");
                }
                if (store != null) {
                    store.close();  // Closes store safely
                    logger.info("Email store connection closed.");
                }
            } catch (MessagingException e) {
                logger.error("Error closing email resources: {}", e.getMessage(), e);
            }
        }


    }


    public void searchEmails(String senderemail) {

        Folder folder = null;

        Store store = null;
        try {
            Session session = createEmailSession();
            store = connectToEmailStore(session);

            folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);

            if (senderemail == null || senderemail.isEmpty()) {
                logger.error("Email search failed: Sender email is empty or null.");
                return;
            }

            SearchTerm searchTerm = new OrTerm(new FromStringTerm(senderemail), new FromStringTerm(senderemail.toLowerCase()));


            Message[] messages = folder.search(searchTerm);

            // ✅ Log how many emails were found before processing them
            logger.info("Total emails found for '{}': {}", senderemail, messages.length);


            if (messages.length == 0) {
                logger.warn("No emails found for '{}'. Ensure the sender address is correct or check the inbox.", senderemail);
                return;
            }


            for (Message message : messages) {
                logger.info("Subject: {}", message.getSubject());


                Object content = message.getContent();
                if (content instanceof String) {
                    logger.info("content: {}", content);
                } else if (content instanceof Multipart) {

                    String extractedContent = "[No readable content]";

                    Multipart multipart = (Multipart) content;

                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        if (bodyPart.isMimeType("text/plain")) {
                            extractedContent = bodyPart.getContent().toString();
                            break;
                        } else if (bodyPart.isMimeType("text/html") && extractedContent.equals("[No readable content]")) {
                            extractedContent = bodyPart.getContent().toString();
                        }

                        // ✅ Detect inline images inside HTML emails
                        if (bodyPart.isMimeType("text/html") && bodyPart.getContent().toString().contains("<img")) {
                            logger.info("Email contains embedded images.");
                        }
                    }

                    logger.info("content: {}", extractedContent);


                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                            logger.info("Attachment found: {}", bodyPart.getFileName());

                            try (InputStream inputStream = bodyPart.getInputStream()) {
                                Path attachmentPath = Paths.get("attachments/", System.currentTimeMillis() + "_" + bodyPart.getFileName());

                                Files.copy(inputStream, attachmentPath , StandardCopyOption.REPLACE_EXISTING);
                                logger.info("Attachment saved: {}", bodyPart.getFileName());
                            } catch (IOException e) {
                                logger.error("Error saving attachment {}: {}", bodyPart.getFileName(), e.getMessage(), e);
                            }
                        } else if (bodyPart.isMimeType("image/*")) {
                            logger.info("Image attachment detected: {}", bodyPart.getFileName());
                        } else if (bodyPart.isMimeType("video/*")) {
                            logger.info("Video attachment detected: {}", bodyPart.getFileName());
                        } else if (bodyPart.isMimeType("application/pdf")) {
                            logger.info("PDF document detected: {}", bodyPart.getFileName());
                        }
                       

                    }
                } else {
                    logger.info("content: [Unsupported content type]");
                }
            }


        } catch (AddressException e) {
            logger.error("Invalid email address format: {}", e.getMessage(), e);
        } catch (MessagingException e) {
            logger.error("Messaging exception occurred while searching emails: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Error reading email content: {}", e.getMessage(), e);
        } finally {
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(false);  // Closes folder safely
                    logger.info("INBOX folder closed.");
                }
                if (store != null) {
                    store.close();  // Closes store safely
                    logger.info("Email store connection closed.");
                }
            } catch (MessagingException e) {
                logger.error("Error closing email resources: {}", e.getMessage(), e);
            }
        }


    }


}

package com.example.demo.serviceimpl;

import com.example.demo.exception.EmailServiceException;
import com.example.demo.helper.EmailMessages;
import com.example.demo.service.Emailservice;
import jakarta.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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

            
        }
        catch (Exception e) {
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
    private EmailMessages processMessage(jakarta.mail.Message message)
    {
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
        }
        catch (MessagingException e)
        {
            logger.error("Messaging exception occurred in processMessage: {}", e.getMessage(), e);

            throw  new EmailServiceException("Failed to retrieve inbox messages"+e);
        }
        catch (IOException e)
        {
            logger.error("IOException exception occured in processmessage: {} ",e.getMessage(),e);
        }
        catch (Exception e)
        {
            logger.error("Exception exception occured in processmessage : {} ",e.getMessage(),e);
        }
        
        return emailMessages;
    }

    /**
     * Creates an email session based on externalized properties.
     */
    private Session createEmailSession()
    {
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
    
}

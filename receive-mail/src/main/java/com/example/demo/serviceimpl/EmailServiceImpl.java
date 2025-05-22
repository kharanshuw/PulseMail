package com.example.demo.serviceimpl;

import com.example.demo.dto.responsedto.EmailDto;
import com.example.demo.exception.*;
import com.example.demo.helper.EmailMessages;
import com.example.demo.service.Emailservice;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.search.*;
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
import java.text.SimpleDateFormat;
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
     * This method connects to the Gmail server using IMAP, accesses the INBOX
     * folder,
     * fetches email subjects, and logs them. It ensures proper resource management
     * by
     * closing connections in a `finally` block.
     * </p>
     *
     * @return A list of {@link EmailMessages} containing the subjects of fetched
     * emails.
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

            boolean fetchSingleEmail = true; // ✅ Make it configurable

            // ✅ Step 6: Processing and logging email subjects

            for (jakarta.mail.Message message : messages) {
                String subject = message.getSubject();
                logger.info("Processing email - Subject: " + subject);
                EmailMessages emailMessages1 = new EmailMessages();
                emailMessages1.setSubject(subject);
                emailMessages.add(emailMessages1);
                if (fetchSingleEmail)
                    break; // ✅ Controlled behavior
            }

            // ✅ Step 7: Logging latest email subject

            Message latestMessage = messages[messages.length - 1];

            logger.info("✔ Latest Email Retrieved | Subject: {}", latestMessage.getSubject());

        } catch (NoSuchProviderException e) {
            logger.error("Email provider error: " + e.getMessage(), e);
            // throw new RuntimeException(e);
        } catch (MessagingException e) {

            logger.error("Messaging error occurred: " + e.getMessage(), e);

            // throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Unexpected error during email retrieval: " + e.getMessage(), e);

            // throw new RuntimeException(e);
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

            // ✅ step 1 and 2 Modularized session and store connection
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

            boolean fetchSingleEmail = true; // ✅ Make it configurable

            // ✅ Step 6: Processing and logging email subjects

            for (jakarta.mail.Message message : messages) {

                try {
                    EmailMessages processedMessage = processMessage(message);
                    emailMessages.add(processedMessage);
                } catch (Exception ex) {
                    logger.error("Error processing a message: {}", ex.getMessage(), ex);
                }
                if (fetchSingleEmail) {
                    break; // Controlled behavior: process only one email
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
     * Processes a single email message by extracting its subject, sender addresses,
     * and content.
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
        // Using Session.getDefaultInstance() ensures the system-wide configuration is
        // applied.

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

        // Get the Store object from the session. The store will be used for accessing
        // mail folders.

        Store store = session.getStore();
        logger.info("Connecting to email store...");

        // Connect to the email store using the username and password.
        // This step may throw MessagingException if the credentials or server are
        // incorrect.
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
     *
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
     * @throws Exception          If any unexpected error occurs during email
     *                            retrieval.
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


    /**
     * Searches for emails from a specific sender in the INBOX folder.
     * Extracts plain text or HTML content, detects inline images, and handles attachments (images, videos, PDFs).
     *
     * @param senderemail The email address of the sender to search for.
     */
    public void searchEmails(String senderemail) {

        Folder folder = null;

        Store store = null;
        try {
            // ✅ Start process: Log method execution

            logger.info("Starting searchEmails with sender: {}", senderemail);

            // ✅ Initialize email session


            logger.info("Creating email session...");

            Session session = createEmailSession();

            logger.info("Email session created.");


            // ✅ Connect to email store

            logger.info("Connecting to email store...");

            store = connectToEmailStore(session);
            logger.info("Connected to email store.");

            // ✅ Open INBOX folder in read-only mode
            logger.info("Retrieving INBOX folder...");

            folder = store.getFolder("INBOX");

            logger.info("Opening INBOX folder in READ_ONLY mode...");

            folder.open(Folder.READ_ONLY);


            // ✅ Validate sender email before proceeding

            if (senderemail == null || senderemail.isEmpty()) {
                logger.error("Email search failed: Sender email is empty or null.");
                return;
            }

            // ✅ Create search term for sender email

            logger.info("Creating search term for sender: {}", senderemail);

            SearchTerm searchTerm = new OrTerm(new FromStringTerm(senderemail),
                    new FromStringTerm(senderemail.toLowerCase()));


            // ✅ Execute search in INBOX

            logger.info("Executing search with search term...");

            Message[] messages = folder.search(searchTerm);

            logger.info("Search completed. Total emails found for '{}': {}", senderemail, messages.length);

            // ✅ If no matching emails found, log and exit

            if (messages.length == 0) {
                logger.warn("No emails found for '{}'. Ensure the sender address is correct or check the inbox.",
                        senderemail);
                return;
            }
            // ✅ Process each found email message


            logger.info("Processing {} message(s)...", messages.length);

            for (Message message : messages) {


                logger.info("Processing message with subject: {}", message.getSubject());


                // ✅ Extract email content
                Object content = message.getContent();


                if (content instanceof String) {


                    logger.info("Content extracted as plain text: {}", content);


                } else if (content instanceof Multipart) {

                    logger.info("Multipart content detected. Extracting content parts...");

                    String extractedContent = "[No readable content]";

                    Multipart multipart = (Multipart) content;

                    // ✅ Extract email content (text/plain preferred, fallback to text/html)
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);

                        logger.info("Processing part {} with MIME type: {}", i, bodyPart.getContentType());


                        if (bodyPart.isMimeType("text/plain")) {

                            logger.info("Text/plain part found. Extracting text...");
                            extractedContent = bodyPart.getContent().toString();
                            break;

                            // ✅ Detect inline images within HTML emails
                        } else if (bodyPart.isMimeType("text/html")
                                && extractedContent.equals("[No readable content]")) {
                            logger.info("Text/html part found. Using HTML content as fallback...");
                            extractedContent = bodyPart.getContent().toString();
                        }

                        // ✅ Detect inline images inside HTML emails
                        if (bodyPart.isMimeType("text/html") && bodyPart.getContent().toString().contains("<img")) {

                            logger.info("Inline image detected in HTML content.");

                        }
                    }

                    logger.info("Extracted content: {}", extractedContent);

                    // ✅ Handle attachments (images, videos, PDFs)
                    logger.info("Checking for attachments in multipart content...");


                    for (int i = 0; i < multipart.getCount(); i++) {

                        BodyPart bodyPart = multipart.getBodyPart(i);

                        // ✅ Detect file attachments

                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {


                            logger.info("Attachment found: {}", bodyPart.getFileName());

                            try (InputStream inputStream = bodyPart.getInputStream()) {
                                Path attachmentPath = Paths.get("attachments/",
                                        System.currentTimeMillis() + "_" + bodyPart.getFileName());

                                Files.copy(inputStream, attachmentPath, StandardCopyOption.REPLACE_EXISTING);
                                logger.info("Attachment saved: {}", bodyPart.getFileName());


                                // ✅ Read attachment into byte array before saving
                                byte[] filedata = inputStream.readAllBytes();

                                logger.info("Attachment '{}' detected - Size: {} KB", bodyPart.getFileName(),
                                        filedata.length / 1024);
                            } catch (IOException e) {
                                logger.error("Error saving attachment {}: {}", bodyPart.getFileName(), e.getMessage(),
                                        e);
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
                // ✅ Cleanup resources (Closing folder and store)
                if (folder != null && folder.isOpen()) {
                    folder.close(false); // Closes folder safely
                    logger.info("INBOX folder closed.");
                }
                if (store != null) {
                    store.close(); // Closes store safely
                    logger.info("Email store connection closed.");
                }
            } catch (MessagingException e) {
                logger.error("Error closing email resources: {}", e.getMessage(), e);
            }
        }

    }



    /**
     * Deletes all emails in the INBOX received from a specific sender.
     * <p>
     * This method connects to the email store using IMAP, searches the "INBOX" folder
     * for messages from the given sender, marks them as deleted, and closes the folder
     * with expunge to permanently remove them. If the sender email is blank or null,
     * or if there is an error during the process, a custom {@link EmailDeletionException} is thrown.
     * </p>
     *
     * @param senderEmails the email address of the sender whose emails should be deleted
     * @throws EmailDeletionException if the deletion fails due to connection or messaging errors
     */
    public void deleteEmailsFromSender(String senderEmails) {
        Session session = null;
        Folder folder = null;
        Store store = null;

        logger.info("Starting email deletion process for sender: {}", senderEmails);

        try {
            // Validate sender email input

            if (senderEmails == null || senderEmails.isBlank()) {
                logger.warn("No sender email provided. Aborting delete operation.");

                throw new EmailDeletionException("Sender email must not be null or blank.");


            }
            // Step 1: Create the email session with IMAP properties
            logger.info("Creating email session...");

            session = createEmailSession();

            // Step 2: Connect to the email store using credentials
            logger.info("Connecting to email store...");

            store = connectToEmailStore(session);


            // Step 3: Access the INBOX folder in READ_WRITE mode
            logger.info("Accessing 'INBOX' folder...");

            folder = store.getFolder("INBOX");

            folder.open(Folder.READ_WRITE);

            logger.info("'INBOX' folder opened in READ_WRITE mode.");

            // Step 4: Search for emails from the specified sender
            logger.info("Searching for emails from sender: {}", senderEmails);

            Message[] messages = folder.search(new FromTerm(new InternetAddress(senderEmails)));


            // Step 5: Exit early if no messages are found

            if (messages.length == 0) {
                logger.info("No emails found from sender '{}'. Nothing to delete.", senderEmails);
                return;
            }


            // Step 6: Mark each matching email as deleted
            int deletedCount = 0;

            for (Message message : messages) {
                message.setFlag(Flags.Flag.DELETED, true);
                deletedCount++;

                logger.info("Marked email for deletion: Subject='{}'", message.getSubject());

            }


            logger.info("Marked {} emails from '{}' for deletion.", deletedCount, senderEmails);


        } catch (Exception e) {

// Step 7: Wrap and rethrow exception as a custom exception for consistent error handling
            logger.error("Failed to delete emails from sender '{}': {}", senderEmails, e.getMessage(), e);

            throw new EmailDeletionException("Failed to delete emails from sender: " + senderEmails, e);

        } finally {
            // Step 8: Safely close folder and store, with fallback if expunge fails
            try {
                if (folder != null && folder.isOpen()) {
                    try {
                        folder.close(true);// Expunge messages marked as deleted
                        logger.info("Folder closed with expunge (deleted messages permanently).");
                    } catch (MessagingException ex) {
                        logger.warn("Expunge failed. Closing folder without deleting flagged messages.");
                        
                        folder.close(false);  // Close without expunging

                        logger.info("Folder closed without expunging.");
                    }
                }
                if (store != null) {
                    store.close();

                    logger.info("Email store connection closed.");
                }
            } catch (MessagingException me) {
                logger.warn("Error closing folder/store: {}", me.getMessage(), me);
            }
        }


        logger.info("Email deletion process completed for sender: {}", senderEmails);


    }


    /**
     * Builds a composite SearchTerm based on optional filters like sender, subject, unread status, and since date.
     *
     * @param sender       Filter by sender's email address (can be null)
     * @param subject      Filter by subject keyword (can be null)
     * @param unread       Filter for unread emails if true (can be null)
     * @param sinceDateStr Filter for emails received on or after this date (format: yyyy-MM-dd)
     * @return A combined SearchTerm or null if no filters are applied
     */
    private SearchTerm buildSearchTerm(String sender, String subject, Boolean unread, String sinceDateStr) {
        List<SearchTerm> terms = new ArrayList<>();

        try {
            // Add sender filter
            logger.debug("Building search term with parameters - sender: {}, subject: {}, unread: {}, sinceDate: {}",
                    sender, subject, unread, sinceDateStr);
            
            
            if (sender != null && !sender.isBlank()) {
                terms.add(new FromTerm(new InternetAddress(sender)));

                logger.info("Added sender filter to search term.");
            }

            // Add subject filter

            if (subject != null && !subject.isBlank()) {
                terms.add(new SubjectTerm(subject));

                logger.info("Added subject filter to search term.");
            }

            // Add unread filter
            if (unread != null && unread) {
                terms.add(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                logger.info("Added unread filter to search term.");
            }

            // Add since date filter
            if (sinceDateStr != null) {
                Date sinceDate = new SimpleDateFormat("yyyy-MM-dd").parse(sinceDateStr);
                terms.add(new ReceivedDateTerm(ComparisonTerm.GE, sinceDate));

                logger.info("Added sinceDate filter to search term.");
            }

            if (terms.isEmpty()) {
                logger.info("No filters provided. Returning null SearchTerm to fetch all emails.");
                return null;
            }

            // Combine filters using AND
            SearchTerm finalTerm = terms.get(0);
            for (int i = 1; i < terms.size(); i++) {
                finalTerm = new AndTerm(finalTerm, terms.get(i));
            }



            logger.info("Search term built successfully with {} conditions.", terms.size());
            return finalTerm;
        } catch (Exception e) {
            logger.warn("Error parsing search filters. Returning all emails. Error: {}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * Fetches emails from the user's inbox using optional filters.
     *
     * @param sender    Optional sender email to filter
     * @param subject   Optional subject keyword to filter
     * @param unread    If true, fetch only unread emails
     * @param limit     Max number of emails to return (default to 10 if non-positive)
     * @param sinceDate Optional date in yyyy-MM-dd format to filter emails received since
     * @return List of filtered EmailDto objects
     */
    public List<EmailDto> fetchEmails(String sender,String subject,Boolean unread,int limit,String sinceDate)
    {

        logger.info("Fetching emails with filters - sender: {}, subject: {}, unread: {}, limit: {}, sinceDate: {}",
                sender, subject, unread, limit, sinceDate);
        
        
        List<EmailDto> results = new ArrayList<>();
        Session session = createEmailSession();

        logger.info("Email session created successfully.");



        try (Store store = connectToEmailStore(session)) {
            Folder inbox = store.getFolder("[Gmail]/All Mail");


            logger.info("Connected to email store.");

            
            try {
                inbox.open(Folder.READ_ONLY);
                
                
                logger.info("INBOX folder opened successfully.");


                // Build search term based on filters
                SearchTerm searchTerm = buildSearchTerm(sender, subject, unread, sinceDate);
                
                
                
                Message[] messages = (searchTerm != null)
                        ? inbox.search(searchTerm)
                        : inbox.getMessages();

                logger.info("Found {} email(s) matching the criteria.", messages.length);
                
                

                // Sort by received date descending
                Arrays.sort(messages, Comparator.comparing((Message m) -> {
                    try {
                        return Optional.ofNullable(m.getReceivedDate()).orElse(new Date(0));
                    } catch (MessagingException e) {
                        logger.warn("Failed to get received date", e);
                        return new Date(0);
                    }
                }).reversed());


                int safeLimit = limit > 0 ? limit : 10;

                logger.info("Processing up to {} email(s).", safeLimit);
                

                // Limit and transform to DTO
                for (int i = 0; i < Math.min(safeLimit, messages.length); i++) {
                    
                    
                    Message message = messages[i];
                    EmailDto emailDto = new EmailDto();

                    try {
                        emailDto.setSubject(message.getSubject());
                        
                        
                        Address[] fromAddresses = message.getFrom();
                        
                        
                        if (fromAddresses != null && fromAddresses.length > 0) {
                            emailDto.setSender(fromAddresses[0].toString());
                        }
                        Date receivedDate = message.getReceivedDate();
                        emailDto.setReceivedDate(receivedDate != null ? receivedDate.toString() : "N/A");
                        emailDto.setRead(message.isSet(Flags.Flag.SEEN));
                        
                        if (inbox instanceof UIDFolder)
                        {
                            logger.info("getting uid for current messages");
                            UIDFolder uidFolder = (UIDFolder) inbox;
                            
                            long uid = uidFolder.getUID(message);
                            
                            emailDto.setUid(uid);
                        }

                        results.add(emailDto);

                        logger.info("Processed message #{}: subject='{}'", i + 1, message.getSubject());

                    } catch (Exception ex) {
                        logger.warn("Failed to parse message #{}: {}", i + 1, ex.getMessage(), ex);
                    }
                }

            } finally {
                // Always close the folder safely
                if (inbox != null && inbox.isOpen()) {
                    inbox.close(false); // Don't expunge since it's read-only
                    logger.info("INBOX folder closed.");
                }
            }

        } catch (Exception e) {
            logger.error("Error while fetching emails: {}", e.getMessage(), e);
            throw new EmailFetchException("Failed to fetch emails", e);
        }


        logger.info("Returning {} email(s) to the client.", results.size());

        return results;
    }

    /**
     * Marks an email as read or unread based on its UID.
     *
     * This method connects to the Gmail "[Gmail]/All Mail" folder, retrieves the email message by its unique UID,
     * and updates its read status flag (SEEN) accordingly.
     *
     * @param uid  the unique identifier of the email message to update
     * @param read true to mark the email as read, false to mark it as unread
     * @throws EmailStatusUpdateException if any error occurs while updating the email status
     */
    public void markEmailReadStatus(long uid, boolean read)
    {

        logger.info("Starting to mark email UID {} as {}", uid, read ? "READ" : "UNREAD");

        
        // Create email session

        Session session = createEmailSession();

        Folder folder = null;
        
        try(Store store = connectToEmailStore(session)) {

            logger.info("Connected to email store.");

            // Access the "[Gmail]/All Mail" folder (Gmail specific folder)
            folder = store.getFolder("[Gmail]/All Mail");


            // Open folder in read-write mode to allow flag modification

            folder.open(Folder.READ_WRITE);

            logger.info("Folder opened in READ_WRITE mode.");
            
            

            // Check if the folder supports UID operations
            if (folder instanceof  UIDFolder)
            {
                logger.info("Selected folder is instance of UIDFolder.");
            }
            else {
                logger.error("Selected folder is NOT instance of UIDFolder.");
                
                throw  new MessagingException("Folder does not support UIDs");
            }
            
            UIDFolder uidFolder =(UIDFolder) folder;

            // Retrieve the message by its unique UID

            Message message = uidFolder.getMessageByUID(uid);
            
            if (message == null)
            {
                logger.error("Message with UID {} not found.", uid);

                throw new EmailNotFoundException("Message with UID " + uid + " not found.");
            }


            logger.info("Message with UID {} retrieved successfully.", uid);
            
            
            // Set or unset the SEEN flag to mark read/unread status

            message.setFlag(Flags.Flag.SEEN,read);


            logger.info("Email with UID {} marked as {}", uid, read ? "READ" : "UNREAD");
            
            
            
        } catch (Exception e) {
            logger.error("Failed to update read status for UID {}: {}", uid, e.getMessage(), e);
            throw new EmailStatusUpdateException("Could not update email status", e);
        }
        finally {

            // Ensure folder is closed properly to release resources

            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(false); // false = do not expunge deleted messages

                    logger.info("Folder closed successfully.");
                }

            } catch (MessagingException e) {

                // Log warning if closing folder fails, but do not rethrow to avoid crashing

                logger.warn("Failed to close folder: {}", e.getMessage(), e);
                
            }
        }
    }
    
    
    

}

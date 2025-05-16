package com.example.demo.serviceimpl;

import com.example.demo.helper.EmailMessages;
import com.example.demo.service.Emailservice;
import jakarta.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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


    @Override
    public List<EmailMessages> getInboxMessages() {

        try {
            Properties properties = new Properties();

            properties.setProperty("mail.store.protocol", protocol);

            properties.setProperty("mail.imaps.host", host);

            properties.setProperty("mail.imaps.port", port);

            Session session = Session.getDefaultInstance(properties);

            Store store = session.getStore();
            
            store.connect(username,password);

            Folder folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);

            jakarta.mail.Message message[] = folder.getMessages();

            for (jakarta.mail.Message message1 : message) {
                logger.info(message1.getSubject());
                logger.info("-------------------------------------------------");
                break;
            }


            Message message1 = message[message.length - 1];
    
            logger.info("printing lestest mail subject"+message1.getSubject());

            return null;

        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    
    
    
}

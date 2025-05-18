package com.example.demo.exception;

import com.example.demo.serviceimpl.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailServiceException extends RuntimeException{

    private Logger logger = LoggerFactory.getLogger(EmailServiceException.class);
    
    
    public EmailServiceException() {
        logger.error("EmailServiceException exception occured");
    }

    public EmailServiceException(String message) {
        super(message);
    }
}

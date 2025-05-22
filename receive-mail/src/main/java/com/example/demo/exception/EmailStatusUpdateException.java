package com.example.demo.exception;

public class EmailStatusUpdateException extends  RuntimeException{
    public EmailStatusUpdateException() {
    }

    public EmailStatusUpdateException(String message) {
        super(message);
    }

    public EmailStatusUpdateException(String message,Throwable throwable)
    {
        super(message,throwable);
    }
    
    
    
}

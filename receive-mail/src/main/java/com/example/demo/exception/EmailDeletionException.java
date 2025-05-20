package com.example.demo.exception;

public class EmailDeletionException extends  RuntimeException{
    public EmailDeletionException(String message) {
        super(message);
    }

    public EmailDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}

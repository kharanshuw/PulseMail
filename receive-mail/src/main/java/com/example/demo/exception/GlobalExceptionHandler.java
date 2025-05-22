package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailDeletionException.class)
    public ResponseEntity<String> handleEmailDeletionException(EmailDeletionException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Email deletion failed: " + ex.getMessage());
    }


    /**
     * Handles EmailFetchException thrown when emails cannot be fetched.
     */
    @ExceptionHandler(EmailFetchException.class)
    public ResponseEntity<Object> handleEmailFetchException(EmailFetchException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch emails");
    }

    /**
     * Helper method to construct a structured error response.
     */
    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("details", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    /**
     * Handles EmailStatusUpdateException thrown when an email's read/unread status cannot be updated.
     */
    @ExceptionHandler(EmailStatusUpdateException.class)
    public ResponseEntity<Object> handleEmailStatusUpdateException(EmailStatusUpdateException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update email read status");
    }

    /**
     * Handles all other unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }


    /**
     * Handles {@link com.example.demo.exception.EmailNotFoundException} thrown when an email with the specified UID is not found.
     * <p>
     * This method constructs a 404 Not Found HTTP response with a descriptive error message.
     * It is triggered automatically by Spring when {@code EmailNotFoundException} is thrown
     * from any controller method.
     *
     * @param ex      the exception that was thrown, containing details about the missing email
     * @param request the current web request, used to extract request context information
     * @return a {@link ResponseEntity} containing a structured error response with HTTP status 404
     */
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<Object> handleEmailNotFoundException(EmailNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Email not found");
    }
    
    
    
}
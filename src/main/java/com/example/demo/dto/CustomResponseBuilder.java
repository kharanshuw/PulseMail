package com.example.demo.dto;

import org.springframework.http.HttpStatus;

public class CustomResponseBuilder {
    private String message;

    private HttpStatus status;

    private boolean success = false;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "CustomResponseBuilder [message=" + message + ", httpStatus=" + this.status + ", success=" + success
                + "]";
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }


    

}

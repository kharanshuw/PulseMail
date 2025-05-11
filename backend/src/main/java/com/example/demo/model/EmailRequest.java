package com.example.demo.model;

public class EmailRequest {
    private String toString;

    private String subjectString;

    private String messageString;

    public String getToString() {
        return toString;
    }

    public void setToString(String toString) {
        this.toString = toString;
    }

    public String getSubjectString() {
        return subjectString;
    }

    public void setSubjectString(String subjectString) {
        this.subjectString = subjectString;
    }

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    @Override
    public String toString() {
        return "EmailRequest [toString=" + toString + ", subjectString=" + subjectString + ", messageString="
                + messageString + "]";
    }

    
}

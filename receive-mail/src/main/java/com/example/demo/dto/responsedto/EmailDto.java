package com.example.demo.dto.responsedto;

public class EmailDto {

    private String subject;
    private String sender;
    private String receivedDate;
    private boolean isRead;

    private long uid;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public EmailDto() {
    }


    public EmailDto(String subject, String sender, String receivedDate, boolean isRead) {
        this.subject = subject;
        this.sender = sender;
        this.receivedDate = receivedDate;
        this.isRead = isRead;
    }


    @Override
    public String toString() {
        return "EmailDto{" +
                "subject='" + subject + '\'' +
                ", sender='" + sender + '\'' +
                ", receivedDate='" + receivedDate + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}

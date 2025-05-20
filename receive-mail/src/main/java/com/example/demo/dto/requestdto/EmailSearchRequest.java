package com.example.demo.dto.requestdto;

public class EmailSearchRequest {

    private String sender;
    private String subject;
    private Boolean unread;
    private Integer limit;
    private String sinceDate; // Optional: "yyyy-MM-dd"


    public EmailSearchRequest(String sender, String subject, Boolean unread, Integer limit, String sinceDate) {
        this.sender = sender;
        this.subject = subject;
        this.unread = unread;
        this.limit = limit;
        this.sinceDate = sinceDate;
    }

    public EmailSearchRequest() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getUnread() {
        return unread;
    }

    public void setUnread(Boolean unread) {
        this.unread = unread;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(String sinceDate) {
        this.sinceDate = sinceDate;
    }

    @Override
    public String toString() {
        return "EmailSearchRequest{" +
                "sender='" + sender + '\'' +
                ", subject='" + subject + '\'' +
                ", unread=" + unread +
                ", limit=" + limit +
                ", sinceDate='" + sinceDate + '\'' +
                '}';
    }
}

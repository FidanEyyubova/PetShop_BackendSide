package com.petshop.backend.dto.response.content;

import java.time.LocalDateTime;

public class ContactMessageResponse {

    private Long id;
    private String fullName;
    private String email;
    private String subject;
    private String message;
    private String replyMessage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime repliedAt;

    public ContactMessageResponse() {}

    public ContactMessageResponse(Long id, String fullName, String email, String subject, String message, String replyMessage, String status, LocalDateTime createdAt, LocalDateTime repliedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.replyMessage = replyMessage;
        this.status = status;
        this.createdAt = createdAt;
        this.repliedAt = repliedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReplyMessage() { return replyMessage; }
    public void setReplyMessage(String replyMessage) { this.replyMessage = replyMessage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }
}
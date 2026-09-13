package com.petshop.backend.dto.request.content;

import jakarta.validation.constraints.NotBlank;

public class ContactMessageReplyRequest {

    @NotBlank(message = "Reply message is required")
    private String replyMessage;

    public ContactMessageReplyRequest() {}

    public ContactMessageReplyRequest(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getReplyMessage() { return replyMessage; }
    public void setReplyMessage(String replyMessage) { this.replyMessage = replyMessage; }
}
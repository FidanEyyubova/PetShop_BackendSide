package com.petshop.backend.dto.request.common;

import jakarta.validation.constraints.NotBlank;

public class ReviewReplyRequest {
    @NotBlank(message = "Provider reply content is required")
    private String providerReply;

    public ReviewReplyRequest() {}
    public ReviewReplyRequest(String providerReply) {
        this.providerReply = providerReply;
    }

    public String getProviderReply() { return providerReply; }
    public void setProviderReply(String providerReply) { this.providerReply = providerReply; }
}
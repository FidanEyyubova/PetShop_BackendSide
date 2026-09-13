package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.content.ContactMessageReplyRequest;
import com.petshop.backend.dto.request.content.ContactMessageRequest;
import com.petshop.backend.dto.response.content.ContactMessageResponse;

import java.util.List;

public interface ContactMessageService {
    ContactMessageResponse sendContactMessage(ContactMessageRequest request);
    List<ContactMessageResponse> getAllContactMessages();
    ContactMessageResponse getContactMessageById(Long id);
    ContactMessageResponse replyToMessage(Long id, ContactMessageReplyRequest request);
    void deleteContactMessage(Long id);
}
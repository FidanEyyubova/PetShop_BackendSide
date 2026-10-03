package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.content.ContactMessageReplyRequest;
import com.petshop.backend.dto.request.content.ContactMessageRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.content.ContactMessageResponse;

public interface ContactMessageService {
    ContactMessageResponse sendContactMessage(ContactMessageRequest request);
    PageResponse<ContactMessageResponse> getAllContactMessages(String status, int page, int size);
    ContactMessageResponse getContactMessageById(Long id);
    ContactMessageResponse replyToMessage(Long id, ContactMessageReplyRequest request);
    void deleteContactMessage(Long id);
}

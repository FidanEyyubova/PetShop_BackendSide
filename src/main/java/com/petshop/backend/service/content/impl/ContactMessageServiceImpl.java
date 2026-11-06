package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.content.ContactMessageReplyRequest;
import com.petshop.backend.dto.request.content.ContactMessageRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.content.ContactMessageResponse;
import com.petshop.backend.entity.content.ContactMessage;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.ContactMessageRepository;
import com.petshop.backend.service.content.ContactMessageService;
import com.petshop.backend.service.notification.EmailService;
import com.petshop.backend.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository repository;
    private final EmailService emailService;

    public ContactMessageServiceImpl(ContactMessageRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public ContactMessageResponse sendContactMessage(ContactMessageRequest request) {
        ContactMessage message = new ContactMessage();
        message.setFullName(request.getFullName().trim());
        message.setEmail(request.getEmail().trim().toLowerCase());
        message.setSubject(request.getSubject() == null ? null : request.getSubject().trim());
        message.setMessage(request.getMessage().trim());
        message.setStatus("PENDING");
        return toResponse(repository.save(message));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContactMessageResponse> getAllContactMessages(String status, int page, int size) {
        var pageable = PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ContactMessage> result = status == null || status.isBlank()
                ? repository.findAll(pageable)
                : repository.findByStatus(status.trim().toUpperCase(), pageable);
        return PageResponse.of(result, ContactMessageServiceImpl::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactMessageResponse getContactMessageById(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public ContactMessageResponse replyToMessage(Long id, ContactMessageReplyRequest request) {
        ContactMessage message = find(id);
        message.setReplyMessage(request.getReplyMessage().trim());
        message.setStatus("REPLIED");
        message.setRepliedAt(LocalDateTime.now());
        ContactMessage saved = repository.save(message);
        emailService.send(saved.getEmail(),
                "Re: " + (saved.getSubject() == null || saved.getSubject().isBlank() ? "Your message to PetShop" : saved.getSubject()),
                "Hello " + saved.getFullName() + ",\n\n" + saved.getReplyMessage() + "\n\n---\nYour message:\n" + saved.getMessage());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteContactMessage(Long id) {
        repository.delete(find(id));
    }

    private ContactMessage find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact message", "id", id));
    }

    private static ContactMessageResponse toResponse(ContactMessage m) {
        return new ContactMessageResponse(m.getId(), m.getFullName(), m.getEmail(), m.getSubject(), m.getMessage(),
                m.getReplyMessage(), m.getStatus(), m.getCreatedAt(), m.getRepliedAt());
    }
}

package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.content.ContactMessageReplyRequest;
import com.petshop.backend.dto.request.content.ContactMessageRequest;
import com.petshop.backend.dto.response.content.ContactMessageResponse;
import com.petshop.backend.entity.content.ContactMessage;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.ContactMessageRepository;
import com.petshop.backend.service.content.ContactMessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository repository;

    public ContactMessageServiceImpl(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public ContactMessageResponse sendContactMessage(ContactMessageRequest request) {
        ContactMessage message = new ContactMessage();
        message.setFullName(request.getFullName());
        message.setEmail(request.getEmail());
        message.setSubject(request.getSubject());
        message.setMessage(request.getMessage());
        message.setStatus("PENDING");

        ContactMessage saved = repository.save(message);
        return mapToResponse(saved);
    }

    @Override
    public List<ContactMessageResponse> getAllContactMessages() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ContactMessageResponse getContactMessageById(Long id) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));
        return mapToResponse(message);
    }

    @Override
    public ContactMessageResponse replyToMessage(Long id, ContactMessageReplyRequest request) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));

        message.setReplyMessage(request.getReplyMessage());
        message.setStatus("REPLIED");
        message.setRepliedAt(LocalDateTime.now());

        ContactMessage updated = repository.save(message);
        return mapToResponse(updated);
    }

    @Override
    public void deleteContactMessage(Long id) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));
        repository.delete(message);
    }

    private ContactMessageResponse mapToResponse(ContactMessage msg) {
        return new ContactMessageResponse(
                msg.getId(),
                msg.getFullName(),
                msg.getEmail(),
                msg.getSubject(),
                msg.getMessage(),
                msg.getReplyMessage(),
                msg.getStatus(),
                msg.getCreatedAt(),
                msg.getRepliedAt()
        );
    }
}
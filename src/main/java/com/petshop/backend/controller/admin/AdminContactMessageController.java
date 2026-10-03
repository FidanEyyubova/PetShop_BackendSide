package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.ContactMessageReplyRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.ContactMessageResponse;
import com.petshop.backend.service.content.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.petshop.backend.dto.response.common.PageResponse;

@RestController
@RequestMapping("/api/v1/admin/contact-messages")
@Tag(name = "Admin - Content", description = "Site content, blog, contact messages and review moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminContactMessageController {

    private final ContactMessageService contactMessageService;

    public AdminContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping
    @Operation(summary = "List contact messages", description = "Messages sent from the contact form, newest first. Filter by status PENDING or REPLIED.")
    public ResponseEntity<ApiResponse<PageResponse<ContactMessageResponse>>> getAllContactMessages(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Contact messages fetched",
                contactMessageService.getAllContactMessages(status, page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a contact message", description = "One message with its reply.")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> getContactMessageById(@PathVariable Long id) {
        ContactMessageResponse response = contactMessageService.getContactMessageById(id);
        return ResponseEntity.ok(ApiResponse.success("Contact message retrieved successfully", response));
    }

    @PostMapping("/{id}/reply")
    @Operation(summary = "Reply to a contact message", description = "Stores the reply, marks the message REPLIED and emails the sender (when SMTP is configured).")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> replyToMessage(
            @PathVariable Long id,
            @Valid @RequestBody ContactMessageReplyRequest request) {
        ContactMessageResponse response = contactMessageService.replyToMessage(id, request);
        return ResponseEntity.ok(ApiResponse.success("Reply sent to message successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contact message", description = "Removes the message.")
    public ResponseEntity<ApiResponse<Void>> deleteContactMessage(@PathVariable Long id) {
        contactMessageService.deleteContactMessage(id);
        return ResponseEntity.ok(ApiResponse.success("Contact message deleted successfully", null));
    }
}
package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.ContactMessageReplyRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.ContactMessageResponse;
import com.petshop.backend.service.content.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/contact-messages")
@Tag(name = "Admin Panel", description = "Unified Admin Panel management endpoints (Hero, Category, SubCategory, Product, Admin-Service, About, Blog, Contact, Messages, Settings)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminContactMessageController {

    private final ContactMessageService contactMessageService;

    public AdminContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping
    @Operation(summary = "Messages - Get all messages sent from contact page")
    public ResponseEntity<ApiResponse<List<ContactMessageResponse>>> getAllContactMessages() {
        List<ContactMessageResponse> response = contactMessageService.getAllContactMessages();
        return ResponseEntity.ok(ApiResponse.success("Contact messages retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Messages - Get message details by ID")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> getContactMessageById(@PathVariable Long id) {
        ContactMessageResponse response = contactMessageService.getContactMessageById(id);
        return ResponseEntity.ok(ApiResponse.success("Contact message retrieved successfully", response));
    }

    @PostMapping("/{id}/reply")
    @Operation(summary = "Messages - Send reply to user message")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> replyToMessage(
            @PathVariable Long id,
            @Valid @RequestBody ContactMessageReplyRequest request) {
        ContactMessageResponse response = contactMessageService.replyToMessage(id, request);
        return ResponseEntity.ok(ApiResponse.success("Reply sent to message successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Messages - Delete contact message by ID")
    public ResponseEntity<ApiResponse<Void>> deleteContactMessage(@PathVariable Long id) {
        contactMessageService.deleteContactMessage(id);
        return ResponseEntity.ok(ApiResponse.success("Contact message deleted successfully", null));
    }
}
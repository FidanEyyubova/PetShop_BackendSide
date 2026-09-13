package com.petshop.backend.controller.content;

import com.petshop.backend.dto.request.content.ContactMessageRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.ContactMessageResponse;
import com.petshop.backend.service.content.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact-messages")
@Tag(name = "Public Contact Form", description = "Public Contact Form Submission")
public class PublicContactFormController {

    private final ContactMessageService contactMessageService;

    public PublicContactFormController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping
    @Operation(summary = "Send message from public contact page")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> sendContactMessage(@Valid @RequestBody ContactMessageRequest request) {
        ContactMessageResponse response = contactMessageService.sendContactMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Your message has been sent successfully", response));
    }
}
package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.ContactInfoRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.ContactInfoResponse;
import com.petshop.backend.service.content.ContactInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/contact-info")
@Tag(name = "Admin - Content", description = "Site content, blog, contact messages and review moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminContactInfoController {

    private final ContactInfoService contactInfoService;

    public AdminContactInfoController(ContactInfoService contactInfoService) {
        this.contactInfoService = contactInfoService;
    }

    @GetMapping
    @Operation(summary = "Get contact info", description = "Address, email, phone, working hours and map URL shown on the contact page.")
    public ResponseEntity<ApiResponse<ContactInfoResponse>> getContactInfo() {
        ContactInfoResponse response = contactInfoService.getContactInfo();
        return ResponseEntity.ok(ApiResponse.success("Contact info retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Save contact info", description = "Creates or updates the single contact-info record.")
    public ResponseEntity<ApiResponse<ContactInfoResponse>> saveContactInfo(@Valid @RequestBody ContactInfoRequest request) {
        ContactInfoResponse response = contactInfoService.saveOrUpdateContactInfo(request);
        return ResponseEntity.ok(ApiResponse.success("Contact info saved successfully", response));
    }
}
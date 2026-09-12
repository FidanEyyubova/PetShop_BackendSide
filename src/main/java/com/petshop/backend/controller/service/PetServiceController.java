package com.petshop.backend.controller.service;

import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.ProviderProfileResponse;
import com.petshop.backend.dto.response.service.ServiceResponse;
import com.petshop.backend.service.service.PetServiceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
public class PetServiceController {

    private final PetServiceService petServiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ServiceResponse>>> getAllServices(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean emergency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ServiceResponse> response = petServiceService.getAllServices(search, categoryId, emergency, pageable);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<ServiceResponse>>builder()
                        .success(true)
                        .message("XidmÉ™tlÉ™r siyahÄ±sÄ±")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(@PathVariable Long id) {
        ServiceResponse response = petServiceService.getServiceById(id);
        return ResponseEntity.ok(
                ApiResponse.<ServiceResponse>builder()
                        .success(true)
                        .message("XidmÉ™t detalÄ±")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/providers/{providerId}")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> getProviderProfile(@PathVariable Long providerId) {
        ProviderProfileResponse response = petServiceService.getProviderProfile(providerId);
        return ResponseEntity.ok(
                ApiResponse.<ProviderProfileResponse>builder()
                        .success(true)
                        .message("XidmÉ™t tÉ™minatÃ§Ä±sÄ± profili")
                        .data(response)
                        .build()
        );
    }
    public PetServiceController(PetServiceService petServiceService) {
        this.petServiceService = petServiceService;
    }
}
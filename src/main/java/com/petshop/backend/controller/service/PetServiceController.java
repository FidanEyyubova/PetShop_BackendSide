package com.petshop.backend.controller.service;

import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.ServiceResponse;
import com.petshop.backend.service.service.PetServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Service Providers", description = "Public /services pages: categories, provider listing and provider page")
public class PetServiceController {

    private final PetServiceService petServiceService;

    public PetServiceController(PetServiceService petServiceService) {
        this.petServiceService = petServiceService;
    }

    @GetMapping
    @Operation(summary = "Search services", description = "Price-list lines of approved providers, cheapest first.")
    public ResponseEntity<ApiResponse<PageResponse<ServiceResponse>>> list(
            @RequestParam(required = false) String q,
            @Parameter(description = "grooming, veterinary or training") @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) Boolean emergency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Services fetched",
                petServiceService.getAllServices(q, serviceType, providerId, emergency, page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a service", description = "One price-list line.")
    public ResponseEntity<ApiResponse<ServiceResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Service fetched", petServiceService.getServiceById(id)));
    }
}

package com.petshop.backend.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Hidden
@RestController
public class HealthController {

    @Hidden
    @Operation(hidden = true)
    @GetMapping("/")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("PetShop Backend is Running!");
    }

    @Hidden
    @Operation(hidden = true)
    @GetMapping({"/swagger", "/swagger/", "/docs", "/docs/"})
    public RedirectView redirectToDocs() {
        return new RedirectView("/swagger-ui/index.html");
    }
}
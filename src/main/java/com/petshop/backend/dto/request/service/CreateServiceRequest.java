package com.petshop.backend.dto.request.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateServiceRequest {
    @NotBlank(message = "Xidmət adı boş ola bilməz")
    private String title;

    private String description;

    @NotNull(message = "Baza qiymət mütləqdir")
    @Min(value = 0, message = "Qiymət mənfi ola bilməz")
    private BigDecimal basePrice;

    private Integer durationMinutes;
    private String address;
    private String amenities;
    private Boolean emergencyService;
    private Long categoryId;
}

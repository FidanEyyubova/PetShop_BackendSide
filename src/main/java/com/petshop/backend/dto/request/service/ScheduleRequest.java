package com.petshop.backend.dto.request.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ScheduleRequest {

    @NotBlank(message = "Day of week is required")
    @Pattern(regexp = "(?i)MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY", message = "dayOfWeek must be MONDAY..SUNDAY")
    @Schema(example = "MONDAY")
    private String dayOfWeek;

    @JsonFormat(pattern = "HH:mm[:ss]")
    @Schema(type = "string", example = "09:00")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm[:ss]")
    @Schema(type = "string", example = "18:00")
    private LocalTime endTime;

    @Schema(description = "false = closed that day", example = "true")
    private Boolean isActive = true;
}

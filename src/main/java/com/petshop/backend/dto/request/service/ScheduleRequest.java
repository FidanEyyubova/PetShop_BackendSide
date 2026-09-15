package com.petshop.backend.dto.request.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;

public class ScheduleRequest {
    @NotBlank(message = "Day of week is required")
    @Schema(example = "MONDAY")
    private String dayOfWeek;

    @JsonFormat(pattern = "HH:mm[:ss]")
    @Schema(type = "string", example = "09:00:00")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm[:ss]")
    @Schema(type = "string", example = "18:00:00")
    private LocalTime endTime;

    private Boolean isActive = true;

    public ScheduleRequest() {}
    public ScheduleRequest(String dayOfWeek, LocalTime startTime, LocalTime endTime, Boolean isActive) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive != null ? isActive : true;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
package com.petshop.backend.dto.request.service;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;

public class ScheduleRequest {
    @NotBlank(message = "Day of week is required")
    private String dayOfWeek;

    private LocalTime startTime;
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
package com.petshop.backend.dto.response.service;

import java.time.LocalTime;

public class ScheduleResponse {
    private Long id;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;

    public ScheduleResponse() {
    }

    public ScheduleResponse(Long id, String dayOfWeek, LocalTime startTime, LocalTime endTime, Boolean isActive) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
    }

    public static ScheduleResponseBuilder builder() {
        return new ScheduleResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public static class ScheduleResponseBuilder {
        private Long id;
        private String dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private Boolean isActive;

        ScheduleResponseBuilder() {
        }

        public ScheduleResponseBuilder id(Long id) { this.id = id; return this; }
        public ScheduleResponseBuilder dayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
        public ScheduleResponseBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public ScheduleResponseBuilder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
        public ScheduleResponseBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }

        public ScheduleResponse build() {
            return new ScheduleResponse(id, dayOfWeek, startTime, endTime, isActive);
        }
    }
}
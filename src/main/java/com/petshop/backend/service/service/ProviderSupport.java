package com.petshop.backend.service.service;

import com.petshop.backend.common.ServiceType;
import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.response.service.*;
import com.petshop.backend.entity.service.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Stream;

/** Shared mapping and derived values for service providers (open now, price range, weekly schedule). */
@Component
public class ProviderSupport {

    private final ZoneId zone;

    public ProviderSupport(AppProperties properties) {
        this.zone = ZoneId.of(properties.getTimezone());
    }

    public boolean isOpenNow(List<Schedule> schedules) {
        ZonedDateTime now = ZonedDateTime.now(zone);
        String today = now.getDayOfWeek().name();
        LocalTime time = now.toLocalTime();
        return schedules.stream()
                .filter(s -> today.equalsIgnoreCase(s.getDayOfWeek()) && !Boolean.FALSE.equals(s.getIsActive()))
                .filter(s -> s.getStartTime() != null && s.getEndTime() != null)
                .anyMatch(s -> s.getEndTime().isAfter(s.getStartTime())
                        ? !time.isBefore(s.getStartTime()) && time.isBefore(s.getEndTime())
                        // overnight, e.g. 20:00 - 02:00
                        : !time.isBefore(s.getStartTime()) || time.isBefore(s.getEndTime()));
    }

    public String todayHours(List<Schedule> schedules) {
        String today = ZonedDateTime.now(zone).getDayOfWeek().name();
        return schedules.stream()
                .filter(s -> today.equalsIgnoreCase(s.getDayOfWeek()))
                .findFirst()
                .map(this::hours)
                .orElse(null);
    }

    /** Monday..Sunday -> "09:00 - 20:00" or "Closed" (frontend weeklySchedule shape). */
    public Map<String, String> weekly(List<Schedule> schedules) {
        Map<String, String> result = new LinkedHashMap<>();
        for (DayOfWeek d : DayOfWeek.values()) {
            String label = d.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            result.put(label, schedules.stream()
                    .filter(s -> d.name().equalsIgnoreCase(s.getDayOfWeek()))
                    .findFirst().map(this::hours).orElse("Closed"));
        }
        return result;
    }

    private String hours(Schedule s) {
        if (Boolean.FALSE.equals(s.getIsActive()) || s.getStartTime() == null || s.getEndTime() == null) return "Closed";
        return String.format("%02d:%02d - %02d:%02d", s.getStartTime().getHour(), s.getStartTime().getMinute(),
                s.getEndTime().getHour(), s.getEndTime().getMinute());
    }

    public BigDecimal priceMin(List<PetService> services) {
        return services.stream().filter(this::active).map(PetService::getBasePrice).filter(Objects::nonNull)
                .min(BigDecimal::compareTo).orElse(null);
    }

    public BigDecimal priceMax(List<PetService> services) {
        return services.stream().filter(this::active)
                .map(s -> s.getPriceMax() != null ? s.getPriceMax() : s.getBasePrice()).filter(Objects::nonNull)
                .max(BigDecimal::compareTo).orElse(null);
    }

    public static String priceLabel(BigDecimal min, BigDecimal max) {
        if (min == null) return null;
        if (max == null || max.compareTo(min) == 0) return "₼ " + plain(min);
        return "₼ " + plain(min) + " - " + plain(max);
    }

    private static String plain(BigDecimal v) {
        return v.stripTrailingZeros().scale() <= 0 ? v.setScale(0, RoundingMode.HALF_UP).toPlainString()
                : v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private boolean active(PetService s) {
        return !Boolean.FALSE.equals(s.getActive());
    }

    public static String slug(String serviceType) {
        if (serviceType == null) return null;
        try {
            return ServiceType.parse(serviceType).getSlug();
        } catch (IllegalArgumentException e) {
            return serviceType.toLowerCase(Locale.ROOT);
        }
    }

    /** Share of the settings-tab fields that are filled in (0-100). */
    public int completeness(ServiceProviderProfile p, int schedules, int services) {
        long filled = Stream.of(p.getCenterName(), p.getServiceType(), p.getBio(), p.getCity(), p.getAddress(),
                        p.getPhone(), p.getWhatsapp(), p.getInstagram(), p.getLogoUrl(), p.getBannerUrl())
                .filter(v -> v != null && !v.isBlank()).count();
        if (p.getAmenities() != null && !p.getAmenities().isEmpty()) filled++;
        if (schedules > 0) filled++;
        if (services > 0) filled++;
        return (int) Math.round(filled * 100.0 / 13);
    }

    public ServiceResponse toService(PetService s) {
        return ServiceResponse.builder()
                .id(s.getId())
                .providerId(s.getProviderId())
                .title(s.getTitle())
                .description(s.getDescription())
                .priceMin(s.getBasePrice())
                .priceMax(s.getPriceMax())
                .priceLabel(priceLabel(s.getBasePrice(), s.getPriceMax()))
                .durationText(s.getDurationText())
                .durationMinutes(s.getDurationMinutes())
                .active(!Boolean.FALSE.equals(s.getActive()))
                .emergencyService(Boolean.TRUE.equals(s.getEmergencyService()))
                .categoryId(s.getCategoryId())
                .createdAt(s.getCreatedAt())
                .build();
    }

    public TeamMemberResponse toTeamMember(TeamMember t) {
        return TeamMemberResponse.builder()
                .id(t.getId())
                .fullName(t.getFullName())
                .role(t.getRole())
                .experience(t.getExperience())
                .certifications(t.getCertifications())
                .bio(t.getBio())
                .note(t.getNote())
                .avatar(t.getAvatar())
                .featured(Boolean.TRUE.equals(t.getFeatured()))
                .build();
    }

    public ScheduleResponse toSchedule(Schedule s) {
        return ScheduleResponse.builder()
                .id(s.getId())
                .dayOfWeek(s.getDayOfWeek())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .isActive(!Boolean.FALSE.equals(s.getIsActive()))
                .build();
    }

    public ProviderGalleryResponse toGallery(ProviderGallery g) {
        return ProviderGalleryResponse.builder()
                .id(g.getId())
                .providerId(g.getProviderId())
                .imageUrl(g.getImageUrl())
                .title(g.getTitle())
                .createdAt(g.getCreatedAt())
                .build();
    }
}

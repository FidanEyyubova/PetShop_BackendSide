package com.petshop.backend.service.service;

import com.petshop.backend.common.Roles;
import com.petshop.backend.common.ServiceType;
import com.petshop.backend.common.UserStatus;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.*;
import com.petshop.backend.entity.service.*;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.service.*;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.util.MaskUtils;
import com.petshop.backend.util.PageUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/** Public provider listing, provider page and service categories. Only approved providers are shown. */
@Service
public class ProviderQueryService {

    private final ServiceProviderProfileRepository profileRepository;
    private final PetServiceRepository serviceRepository;
    private final ScheduleRepository scheduleRepository;
    private final TeamMemberRepository teamRepository;
    private final ProviderGalleryRepository galleryRepository;
    private final UserRepository userRepository;
    private final ProviderSupport support;

    public ProviderQueryService(ServiceProviderProfileRepository profileRepository, PetServiceRepository serviceRepository,
                                ScheduleRepository scheduleRepository, TeamMemberRepository teamRepository,
                                ProviderGalleryRepository galleryRepository, UserRepository userRepository,
                                ProviderSupport support) {
        this.profileRepository = profileRepository;
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.teamRepository = teamRepository;
        this.galleryRepository = galleryRepository;
        this.userRepository = userRepository;
        this.support = support;
    }

    @Transactional(readOnly = true)
    public List<ServiceCategoryResponse> serviceCategories() {
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : profileRepository.countActiveByServiceType()) {
            counts.put(((String) row[0]).toUpperCase(Locale.ROOT), (Long) row[1]);
        }
        return Arrays.stream(ServiceType.values())
                .map(t -> ServiceCategoryResponse.builder()
                        .id(t.getSlug())
                        .type(t.name())
                        .name(t.getLabel())
                        .azName(t.getAzLabel())
                        .providerCount(counts.getOrDefault(t.name(), 0L))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ProviderSummaryResponse> list(String serviceType, String city, String q, Boolean openNow,
                                                      int page, int size) {
        ServiceType type = ServiceType.parse(serviceType);
        Specification<ServiceProviderProfile> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            Subquery<Long> approved = query.subquery(Long.class);
            var user = approved.from(User.class);
            approved.select(user.get("id")).where(
                    cb.equal(user.get("id"), root.get("providerId")),
                    cb.equal(user.get("status"), UserStatus.ACTIVE),
                    cb.equal(user.get("role"), Roles.ROLE_SERVICE_PROVIDER),
                    cb.isNull(user.get("deletedAt")));
            p.add(cb.exists(approved));
            if (type != null) p.add(cb.equal(cb.upper(root.get("serviceType")), type.name()));
            if (city != null && !city.isBlank()) p.add(cb.equal(cb.lower(root.get("city")), city.trim().toLowerCase(Locale.ROOT)));
            if (q != null && !q.isBlank()) {
                String pattern = "%" + MaskUtils.escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
                p.add(cb.or(cb.like(cb.lower(root.get("centerName")), pattern, '\\'),
                        cb.like(cb.lower(cb.coalesce(root.get("bio"), "")), pattern, '\\'),
                        cb.like(cb.lower(cb.coalesce(root.get("district"), "")), pattern, '\\')));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
        Page<ServiceProviderProfile> result = profileRepository.findAll(spec,
                PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "rating", "reviewCount").and(Sort.by("id"))));

        List<Long> ids = result.getContent().stream().map(ServiceProviderProfile::getProviderId).toList();
        Map<Long, List<PetService>> services = ids.isEmpty() ? Map.of()
                : serviceRepository.findByProviderIdInAndActiveTrue(ids).stream().collect(Collectors.groupingBy(PetService::getProviderId));
        Map<Long, List<Schedule>> schedules = ids.isEmpty() ? Map.of()
                : scheduleRepository.findByProviderIdIn(ids).stream().collect(Collectors.groupingBy(Schedule::getProviderId));

        List<ProviderSummaryResponse> content = result.getContent().stream()
                .map(p -> toSummary(p, services.getOrDefault(p.getProviderId(), List.of()),
                        schedules.getOrDefault(p.getProviderId(), List.of())))
                .filter(s -> openNow == null || openNow.equals(s.getIsOpen()))
                .toList();
        return PageResponse.of(result, content);
    }

    /**
     * Public page; 404 for unknown, deleted or not yet approved providers.
     * {@code adminView}: admins also see providers waiting for approval (to review them) and the private fields.
     */
    @Transactional(readOnly = true)
    public ProviderProfileResponse publicProfile(Long providerId, boolean adminView) {
        User user = userRepository.findById(providerId)
                .filter(u -> Roles.ROLE_SERVICE_PROVIDER.equals(Roles.normalize(u.getRole())))
                .filter(u -> !u.isDeleted() && (adminView || u.getStatus() == UserStatus.ACTIVE))
                .orElseThrow(() -> new ResourceNotFoundException("Service provider", "id", providerId));
        ServiceProviderProfile profile = profileRepository.findByProviderId(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Service provider", "id", providerId));
        return buildProfile(user, profile, adminView);
    }

    /** Builds the full profile; {@code owner} adds private fields and shows hidden services and all team members. */
    @Transactional(readOnly = true)
    public ProviderProfileResponse buildProfile(User user, ServiceProviderProfile profile, boolean owner) {
        Long id = user.getId();
        List<PetService> allServices = serviceRepository.findByProviderIdOrderByIdAsc(id);
        List<PetService> visibleServices = owner ? allServices
                : allServices.stream().filter(s -> !Boolean.FALSE.equals(s.getActive())).toList();
        List<Schedule> schedules = scheduleRepository.findByProviderId(id);
        List<TeamMember> team = teamRepository.findByProviderIdOrderByIdAsc(id);
        if (!owner && team.stream().anyMatch(t -> Boolean.TRUE.equals(t.getFeatured()))) {
            team = team.stream().filter(t -> Boolean.TRUE.equals(t.getFeatured())).toList();
        }
        var min = support.priceMin(allServices);
        var max = support.priceMax(allServices);
        return ProviderProfileResponse.builder()
                .providerId(id)
                .fullName(user.getFullName())
                .centerName(profile.getCenterName())
                .serviceType(profile.getServiceType())
                .serviceSlug(ProviderSupport.slug(profile.getServiceType()))
                .bio(profile.getBio())
                .city(profile.getCity())
                .district(profile.getDistrict())
                .address(profile.getAddress())
                .googleMapsUrl(profile.getGoogleMapsUrl())
                .latitude(profile.getLatitude())
                .longitude(profile.getLongitude())
                .phone(profile.getPhone() != null ? profile.getPhone() : user.getPhone())
                .whatsapp(profile.getWhatsapp())
                .instagram(profile.getInstagram())
                .bannerUrl(profile.getBannerUrl())
                .logoUrl(profile.getLogoUrl() != null ? profile.getLogoUrl() : user.getAvatarUrl())
                .amenities(profile.getAmenities())
                .tags(profile.getTags())
                .rating(profile.getRating())
                .reviewCount(profile.getReviewCount() == null ? 0 : profile.getReviewCount())
                .priceMin(min)
                .priceMax(max)
                .priceRange(ProviderSupport.priceLabel(min, max))
                .isOpen(support.isOpenNow(schedules))
                .services(visibleServices.stream().map(support::toService).toList())
                .teamMembers(team.stream().map(support::toTeamMember).toList())
                .schedules(schedules.stream()
                        .sorted(Comparator.comparing(s -> dayIndex(s.getDayOfWeek())))
                        .map(support::toSchedule).toList())
                .weeklySchedule(support.weekly(schedules))
                .gallery(galleryRepository.findByProviderIdOrderByCreatedAtDesc(id).stream().map(support::toGallery).toList())
                .email(owner ? user.getEmail() : null)
                .accountStatus(owner && user.getStatus() != null ? user.getStatus().name() : null)
                .profileCompleteness(owner ? support.completeness(profile, schedules.size(), allServices.size()) : null)
                .build();
    }

    private ProviderSummaryResponse toSummary(ServiceProviderProfile p, List<PetService> services, List<Schedule> schedules) {
        var min = support.priceMin(services);
        var max = support.priceMax(services);
        String bio = p.getBio();
        return ProviderSummaryResponse.builder()
                .id(p.getProviderId())
                .centerName(p.getCenterName())
                .serviceType(p.getServiceType())
                .serviceSlug(ProviderSupport.slug(p.getServiceType()))
                .logoUrl(p.getLogoUrl())
                .coverImage(p.getBannerUrl())
                .description(bio != null && bio.length() > 300 ? bio.substring(0, 297) + "..." : bio)
                .city(p.getCity())
                .district(p.getDistrict())
                .address(p.getAddress())
                .phone(p.getPhone())
                .whatsapp(p.getWhatsapp())
                .instagram(p.getInstagram())
                .rating(p.getRating())
                .reviewCount(p.getReviewCount() == null ? 0 : p.getReviewCount())
                .priceMin(min)
                .priceMax(max)
                .priceRange(ProviderSupport.priceLabel(min, max))
                .isOpen(support.isOpenNow(schedules))
                .workingHours(support.todayHours(schedules))
                .tags(p.getTags())
                .build();
    }

    private static int dayIndex(String day) {
        try {
            return java.time.DayOfWeek.valueOf(day.toUpperCase(Locale.ROOT)).getValue();
        } catch (Exception e) {
            return 8;
        }
    }
}

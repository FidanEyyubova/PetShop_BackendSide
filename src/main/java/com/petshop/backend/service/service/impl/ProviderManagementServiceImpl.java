package com.petshop.backend.service.service.impl;

import com.petshop.backend.common.ServiceType;
import com.petshop.backend.dto.request.service.*;
import com.petshop.backend.dto.response.service.*;
import com.petshop.backend.entity.service.*;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.AccessDeniedCustomException;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.service.*;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.service.ProviderManagementService;
import com.petshop.backend.service.service.ProviderQueryService;
import com.petshop.backend.service.service.ProviderSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProviderManagementServiceImpl implements ProviderManagementService {

    private final ServiceProviderProfileRepository profileRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProviderGalleryRepository galleryRepository;
    private final PetServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final ProviderQueryService queryService;
    private final ProviderSupport support;

    public ProviderManagementServiceImpl(ServiceProviderProfileRepository profileRepository,
                                         TeamMemberRepository teamMemberRepository,
                                         ScheduleRepository scheduleRepository,
                                         ProviderGalleryRepository galleryRepository,
                                         PetServiceRepository serviceRepository,
                                         UserRepository userRepository,
                                         ProviderQueryService queryService,
                                         ProviderSupport support) {
        this.profileRepository = profileRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.scheduleRepository = scheduleRepository;
        this.galleryRepository = galleryRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.queryService = queryService;
        this.support = support;
    }

    // ---- profile -------------------------------------------------------------------------------

    @Override
    @Transactional
    public ProviderProfileResponse getMyProfile(UserPrincipal currentUser) {
        User user = providerUser(currentUser);
        return queryService.buildProfile(user, profileOf(user), true);
    }

    @Override
    @Transactional
    public ProviderProfileResponse updateProviderProfile(UpdateProviderProfileRequest r, UserPrincipal currentUser) {
        User user = providerUser(currentUser);
        ServiceProviderProfile p = profileOf(user);
        if (r.getCenterName() != null) p.setCenterName(r.getCenterName().trim());
        if (r.getServiceType() != null) p.setServiceType(ServiceType.parse(r.getServiceType()).name());
        if (r.getBio() != null) p.setBio(blank(r.getBio()));
        if (r.getCity() != null) p.setCity(blank(r.getCity()));
        if (r.getDistrict() != null) p.setDistrict(blank(r.getDistrict()));
        if (r.getAddress() != null) p.setAddress(blank(r.getAddress()));
        if (r.getGoogleMapsUrl() != null) p.setGoogleMapsUrl(blank(r.getGoogleMapsUrl()));
        if (r.getLatitude() != null) p.setLatitude(r.getLatitude());
        if (r.getLongitude() != null) p.setLongitude(r.getLongitude());
        if (r.getPhone() != null) p.setPhone(blank(r.getPhone()));
        if (r.getWhatsapp() != null) p.setWhatsapp(blank(r.getWhatsapp()));
        if (r.getInstagram() != null) p.setInstagram(blank(r.getInstagram()));
        if (r.getBannerUrl() != null) p.setBannerUrl(blank(r.getBannerUrl()));
        if (r.getLogoUrl() != null) p.setLogoUrl(blank(r.getLogoUrl()));
        if (r.getAmenities() != null) p.setAmenities(clean(r.getAmenities()));
        if (r.getTags() != null) p.setTags(clean(r.getTags()));
        profileRepository.save(p);
        return queryService.buildProfile(user, p, true);
    }

    @Override
    @Transactional
    public ProviderStatsResponse getStats(UserPrincipal currentUser) {
        User user = providerUser(currentUser);
        ServiceProviderProfile p = profileOf(user);
        return ProviderStatsResponse.builder()
                .pageViews(nz(p.getPageViews()))
                .contactClicks(nz(p.getContactClicks()))
                .wishlistAdds(nz(p.getWishlistAdds()))
                .rating(p.getRating())
                .reviewsCount(p.getReviewCount() == null ? 0 : p.getReviewCount())
                .profileCompleteness(support.completeness(p, scheduleRepository.findByProviderId(user.getId()).size(),
                        serviceRepository.findByProviderIdOrderByIdAsc(user.getId()).size()))
                .build();
    }

    // ---- team ----------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeam(UserPrincipal currentUser) {
        Long id = providerId(currentUser);
        return teamMemberRepository.findByProviderIdOrderByIdAsc(id).stream().map(support::toTeamMember).toList();
    }

    @Override
    @Transactional
    public TeamMemberResponse addTeamMember(AddTeamMemberRequest r, UserPrincipal currentUser) {
        Long providerId = providerId(currentUser);
        boolean featured = Boolean.TRUE.equals(r.getFeatured());
        if (featured) checkFeaturedLimit(providerId);
        TeamMember member = TeamMember.builder().providerId(providerId).featured(featured).build();
        applyMember(member, r);
        return support.toTeamMember(teamMemberRepository.save(member));
    }

    @Override
    @Transactional
    public TeamMemberResponse updateTeamMember(Long id, AddTeamMemberRequest r, UserPrincipal currentUser) {
        TeamMember member = ownedMember(id, currentUser);
        applyMember(member, r);
        if (r.getFeatured() != null && r.getFeatured() != Boolean.TRUE.equals(member.getFeatured())) {
            if (r.getFeatured()) checkFeaturedLimit(member.getProviderId());
            member.setFeatured(r.getFeatured());
        }
        return support.toTeamMember(teamMemberRepository.save(member));
    }

    @Override
    @Transactional
    public TeamMemberResponse setFeatured(Long id, boolean featured, UserPrincipal currentUser) {
        TeamMember member = ownedMember(id, currentUser);
        if (featured && !Boolean.TRUE.equals(member.getFeatured())) checkFeaturedLimit(member.getProviderId());
        member.setFeatured(featured);
        return support.toTeamMember(teamMemberRepository.save(member));
    }

    @Override
    @Transactional
    public void deleteTeamMember(Long id, UserPrincipal currentUser) {
        teamMemberRepository.delete(ownedMember(id, currentUser));
    }

    // ---- services (price list) -----------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> getMyServices(UserPrincipal currentUser) {
        return serviceRepository.findByProviderIdOrderByIdAsc(providerId(currentUser)).stream().map(support::toService).toList();
    }

    @Override
    @Transactional
    public ServiceResponse createService(CreateServiceRequest r, UserPrincipal currentUser) {
        PetService s = PetService.builder().providerId(providerId(currentUser)).build();
        applyService(s, r);
        return support.toService(serviceRepository.save(s));
    }

    @Override
    @Transactional
    public ServiceResponse updateService(Long id, CreateServiceRequest r, UserPrincipal currentUser) {
        PetService s = ownedService(id, currentUser);
        applyService(s, r);
        return support.toService(serviceRepository.save(s));
    }

    @Override
    @Transactional
    public ServiceResponse setServiceActive(Long id, boolean active, UserPrincipal currentUser) {
        PetService s = ownedService(id, currentUser);
        s.setActive(active);
        return support.toService(serviceRepository.save(s));
    }

    @Override
    @Transactional
    public void deleteService(Long id, UserPrincipal currentUser) {
        serviceRepository.delete(ownedService(id, currentUser));
    }

    // ---- schedule ------------------------------------------------------------------------------

    /** Replaces the whole week. Days not sent are treated as closed. */
    @Override
    @Transactional
    public List<ScheduleResponse> updateSchedules(List<ScheduleRequest> requests, UserPrincipal currentUser) {
        Long providerId = providerId(currentUser);
        Set<String> seen = new HashSet<>();
        List<Schedule> rows = new ArrayList<>();
        for (ScheduleRequest req : requests) {
            String day = req.getDayOfWeek().trim().toUpperCase(Locale.ROOT);
            if (!seen.add(day)) {
                throw new BadRequestException("DUPLICATE_DAY", day + " appears more than once");
            }
            boolean open = !Boolean.FALSE.equals(req.getIsActive());
            if (open && (req.getStartTime() == null || req.getEndTime() == null)) {
                throw new BadRequestException("INVALID_SCHEDULE", "startTime and endTime are required for open days (" + day + ")");
            }
            if (open && req.getStartTime().equals(req.getEndTime())) {
                throw new BadRequestException("INVALID_SCHEDULE", "startTime and endTime cannot be equal (" + day + ")");
            }
            rows.add(Schedule.builder()
                    .providerId(providerId)
                    .dayOfWeek(day)
                    .startTime(open ? req.getStartTime() : null)
                    .endTime(open ? req.getEndTime() : null)
                    .isActive(open)
                    .build());
        }
        scheduleRepository.deleteByProviderId(providerId);
        return scheduleRepository.saveAll(rows).stream().map(support::toSchedule).toList();
    }

    // ---- gallery -------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ProviderGalleryResponse> getProviderGallery(Long providerId) {
        return galleryRepository.findByProviderIdOrderByCreatedAtDesc(providerId).stream().map(support::toGallery).toList();
    }

    @Override
    @Transactional
    public ProviderGalleryResponse addGalleryImage(AddGalleryImageRequest request, UserPrincipal currentUser) {
        ProviderGallery g = new ProviderGallery();
        g.setProviderId(providerId(currentUser));
        g.setImageUrl(request.getImageUrl().trim());
        g.setTitle(blank(request.getTitle()));
        return support.toGallery(galleryRepository.save(g));
    }

    @Override
    @Transactional
    public void deleteGalleryImage(Long id, UserPrincipal currentUser) {
        ProviderGallery g = galleryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery image", "id", id));
        CurrentUser.requireOwnerOrAdmin(currentUser, g.getProviderId(), "gallery image");
        galleryRepository.delete(g);
    }

    // ---- analytics -----------------------------------------------------------------------------

    @Override
    @Transactional
    public void recordEvent(Long providerId, ProviderEventRequest request) {
        int updated = switch (request.getType().toUpperCase(Locale.ROOT)) {
            case "PAGE_VIEW" -> profileRepository.incrementPageViews(providerId);
            case "CONTACT_CLICK" -> profileRepository.incrementContactClicks(providerId);
            case "WISHLIST_ADD" -> profileRepository.incrementWishlistAdds(providerId);
            default -> throw new BadRequestException("INVALID_EVENT", "Unknown event type");
        };
        if (updated == 0) throw new ResourceNotFoundException("Service provider", "id", providerId);
    }

    // ---- helpers -------------------------------------------------------------------------------

    private Long providerId(UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        if (!currentUser.isServiceProvider() && !currentUser.isAdmin()) {
            throw new AccessDeniedCustomException("Only service providers can manage a center");
        }
        return currentUser.getId();
    }

    private User providerUser(UserPrincipal currentUser) {
        Long id = providerId(currentUser);
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /** Creates the profile on first use (older accounts registered before profiles existed). */
    private ServiceProviderProfile profileOf(User user) {
        return profileRepository.findByProviderId(user.getId()).orElseGet(() -> profileRepository.save(
                ServiceProviderProfile.builder().providerId(user.getId()).centerName(user.getFullName()).phone(user.getPhone()).build()));
    }

    private TeamMember ownedMember(Long id, UserPrincipal currentUser) {
        TeamMember m = teamMemberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Team member", "id", id));
        CurrentUser.requireOwnerOrAdmin(currentUser, m.getProviderId(), "team member");
        return m;
    }

    private PetService ownedService(Long id, UserPrincipal currentUser) {
        PetService s = serviceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        CurrentUser.requireOwnerOrAdmin(currentUser, s.getProviderId(), "service");
        return s;
    }

    private void checkFeaturedLimit(Long providerId) {
        if (teamMemberRepository.countByProviderIdAndFeaturedTrue(providerId) >= TeamMember.FEATURED_LIMIT) {
            throw new ConflictException("FEATURED_LIMIT", "At most " + TeamMember.FEATURED_LIMIT + " team members can be featured");
        }
    }

    private static void applyMember(TeamMember m, AddTeamMemberRequest r) {
        m.setFullName(r.getFullName().trim());
        m.setRole(blank(r.getRole()));
        m.setExperience(blank(r.getExperience()));
        m.setCertifications(blank(r.getCertifications()));
        m.setBio(blank(r.getBio()));
        m.setNote(blank(r.getNote()));
        m.setAvatar(blank(r.getAvatar()));
    }

    private static void applyService(PetService s, CreateServiceRequest r) {
        if (r.getPriceMax() != null && r.getPriceMax().compareTo(r.getPriceMin()) < 0) {
            throw new BadRequestException("INVALID_PRICE_RANGE", "priceMax must be greater than or equal to priceMin");
        }
        s.setTitle(r.getTitle().trim());
        s.setDescription(blank(r.getDescription()));
        s.setBasePrice(r.getPriceMin());
        s.setPriceMax(r.getPriceMax());
        s.setDurationText(blank(r.getDurationText()));
        s.setDurationMinutes(r.getDurationMinutes());
        if (r.getActive() != null) s.setActive(r.getActive());
        if (r.getEmergencyService() != null) s.setEmergencyService(r.getEmergencyService());
        if (r.getCategoryId() != null) s.setCategoryId(r.getCategoryId());
    }

    private static List<String> clean(List<String> values) {
        return new ArrayList<>(values.stream().filter(Objects::nonNull).map(String::trim).filter(v -> !v.isEmpty()).distinct().toList());
    }

    private static String blank(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }

    private static long nz(Long v) {
        return v == null ? 0 : v;
    }
}

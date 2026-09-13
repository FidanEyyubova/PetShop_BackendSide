package com.petshop.backend.service.service.impl;

import com.petshop.backend.dto.request.service.*;
import com.petshop.backend.dto.response.service.*;
import com.petshop.backend.entity.service.*;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.service.*;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.service.ProviderManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderManagementServiceImpl implements ProviderManagementService {

    private final ServiceProviderProfileRepository profileRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProviderGalleryRepository galleryRepository;
    private final UserRepository userRepository;

    public ProviderManagementServiceImpl(
            ServiceProviderProfileRepository profileRepository,
            TeamMemberRepository teamMemberRepository,
            ScheduleRepository scheduleRepository,
            ProviderGalleryRepository galleryRepository,
            UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.scheduleRepository = scheduleRepository;
        this.galleryRepository = galleryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ProviderProfileResponse updateProviderProfile(UpdateProviderProfileRequest request, UserPrincipal currentUser) {
        Long providerId = currentUser.getId();
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider user not found: " + providerId));

        ServiceProviderProfile profile = profileRepository.findByProviderId(providerId)
                .orElseGet(() -> {
                    ServiceProviderProfile p = new ServiceProviderProfile();
                    p.setProviderId(providerId);
                    return p;
                });

        if (request.getCenterName() != null) profile.setCenterName(request.getCenterName());
        if (request.getServiceType() != null) profile.setServiceType(request.getServiceType());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getDistrict() != null) profile.setDistrict(request.getDistrict());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getGoogleMapsUrl() != null) profile.setGoogleMapsUrl(request.getGoogleMapsUrl());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getWhatsapp() != null) profile.setWhatsapp(request.getWhatsapp());
        if (request.getInstagram() != null) profile.setInstagram(request.getInstagram());
        if (request.getBannerUrl() != null) profile.setBannerUrl(request.getBannerUrl());
        if (request.getLogoUrl() != null) profile.setLogoUrl(request.getLogoUrl());

        ServiceProviderProfile saved = profileRepository.save(profile);

        List<ScheduleResponse> schedules = scheduleRepository.findByProviderId(providerId).stream()
                .map(s -> new ScheduleResponse(s.getId(), s.getDayOfWeek(), s.getStartTime(), s.getEndTime(), s.getIsActive()))
                .collect(Collectors.toList());

        List<TeamMemberResponse> teamMembers = teamMemberRepository.findByProviderId(providerId).stream()
                .map(tm -> new TeamMemberResponse(tm.getId(), tm.getFullName(), tm.getRole(), tm.getExperience(), tm.getNote(), tm.getAvatar()))
                .collect(Collectors.toList());

        List<ProviderGalleryResponse> gallery = galleryRepository.findByProviderIdOrderByCreatedAtDesc(providerId).stream()
                .map(g -> new ProviderGalleryResponse(g.getId(), g.getProviderId(), g.getImageUrl(), g.getTitle(), g.getCreatedAt()))
                .collect(Collectors.toList());

        return new ProviderProfileResponse(
                provider.getId(),
                provider.getFullName(),
                saved.getPhone() != null ? saved.getPhone() : provider.getPhone(),
                provider.getEmail(),
                saved.getLogoUrl() != null ? saved.getLogoUrl() : provider.getAvatarUrl(),
                saved.getCenterName(),
                saved.getServiceType(),
                saved.getBio(),
                saved.getCity(),
                saved.getDistrict(),
                saved.getAddress(),
                saved.getGoogleMapsUrl(),
                saved.getWhatsapp(),
                saved.getInstagram(),
                saved.getBannerUrl(),
                saved.getLogoUrl(),
                schedules,
                teamMembers,
                gallery
        );
    }

    @Override
    @Transactional
    public TeamMemberResponse addTeamMember(AddTeamMemberRequest request, UserPrincipal currentUser) {
        Long providerId = currentUser.getId();

        TeamMember teamMember = new TeamMember();
        teamMember.setProviderId(providerId);
        teamMember.setFullName(request.getFullName());
        teamMember.setRole(request.getRole());
        teamMember.setExperience(request.getExperience());
        teamMember.setNote(request.getNote());
        teamMember.setAvatar(request.getAvatar());

        TeamMember saved = teamMemberRepository.save(teamMember);
        return new TeamMemberResponse(saved.getId(), saved.getFullName(), saved.getRole(), saved.getExperience(), saved.getNote(), saved.getAvatar());
    }

    @Override
    @Transactional
    public TeamMemberResponse updateTeamMember(Long id, AddTeamMemberRequest request, UserPrincipal currentUser) {
        TeamMember tm = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found with id: " + id));

        if (!tm.getProviderId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to update this team member");
        }

        tm.setFullName(request.getFullName());
        if (request.getRole() != null) tm.setRole(request.getRole());
        if (request.getExperience() != null) tm.setExperience(request.getExperience());
        if (request.getNote() != null) tm.setNote(request.getNote());
        if (request.getAvatar() != null) tm.setAvatar(request.getAvatar());

        TeamMember saved = teamMemberRepository.save(tm);
        return new TeamMemberResponse(saved.getId(), saved.getFullName(), saved.getRole(), saved.getExperience(), saved.getNote(), saved.getAvatar());
    }

    @Override
    @Transactional
    public void deleteTeamMember(Long id, UserPrincipal currentUser) {
        TeamMember tm = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found with id: " + id));

        if (!tm.getProviderId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to delete this team member");
        }

        teamMemberRepository.delete(tm);
    }

    @Override
    @Transactional
    public List<ScheduleResponse> updateSchedules(List<ScheduleRequest> requests, UserPrincipal currentUser) {
        Long providerId = currentUser.getId();

        List<Schedule> existing = scheduleRepository.findByProviderId(providerId);
        if (!existing.isEmpty()) {
            scheduleRepository.deleteAll(existing);
        }

        List<Schedule> newSchedules = new ArrayList<>();
        for (ScheduleRequest req : requests) {
            Schedule s = new Schedule();
            s.setProviderId(providerId);
            s.setDayOfWeek(req.getDayOfWeek().toUpperCase());
            s.setStartTime(req.getStartTime());
            s.setEndTime(req.getEndTime());
            s.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
            newSchedules.add(s);
        }

        List<Schedule> saved = scheduleRepository.saveAll(newSchedules);
        return saved.stream()
                .map(s -> new ScheduleResponse(s.getId(), s.getDayOfWeek(), s.getStartTime(), s.getEndTime(), s.getIsActive()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderGalleryResponse> getProviderGallery(Long providerId) {
        return galleryRepository.findByProviderIdOrderByCreatedAtDesc(providerId).stream()
                .map(g -> new ProviderGalleryResponse(g.getId(), g.getProviderId(), g.getImageUrl(), g.getTitle(), g.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProviderGalleryResponse addGalleryImage(AddGalleryImageRequest request, UserPrincipal currentUser) {
        Long providerId = currentUser.getId();

        ProviderGallery gallery = new ProviderGallery();
        gallery.setProviderId(providerId);
        gallery.setImageUrl(request.getImageUrl());
        gallery.setTitle(request.getTitle());

        ProviderGallery saved = galleryRepository.save(gallery);
        return new ProviderGalleryResponse(saved.getId(), saved.getProviderId(), saved.getImageUrl(), saved.getTitle(), saved.getCreatedAt());
    }

    @Override
    @Transactional
    public void deleteGalleryImage(Long id, UserPrincipal currentUser) {
        ProviderGallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery image not found with id: " + id));

        if (!gallery.getProviderId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to delete this gallery image");
        }

        galleryRepository.delete(gallery);
    }
}
package com.petshop.backend.service.service;

import com.petshop.backend.dto.request.service.*;
import com.petshop.backend.dto.response.service.*;
import com.petshop.backend.security.UserPrincipal;

import java.util.List;

public interface ProviderManagementService {
    ProviderProfileResponse getMyProfile(UserPrincipal currentUser);
    ProviderProfileResponse updateProviderProfile(UpdateProviderProfileRequest request, UserPrincipal currentUser);
    ProviderStatsResponse getStats(UserPrincipal currentUser);

    List<TeamMemberResponse> getTeam(UserPrincipal currentUser);
    TeamMemberResponse addTeamMember(AddTeamMemberRequest request, UserPrincipal currentUser);
    TeamMemberResponse updateTeamMember(Long id, AddTeamMemberRequest request, UserPrincipal currentUser);
    TeamMemberResponse setFeatured(Long id, boolean featured, UserPrincipal currentUser);
    void deleteTeamMember(Long id, UserPrincipal currentUser);

    List<ServiceResponse> getMyServices(UserPrincipal currentUser);
    ServiceResponse createService(CreateServiceRequest request, UserPrincipal currentUser);
    ServiceResponse updateService(Long id, CreateServiceRequest request, UserPrincipal currentUser);
    ServiceResponse setServiceActive(Long id, boolean active, UserPrincipal currentUser);
    void deleteService(Long id, UserPrincipal currentUser);

    List<ScheduleResponse> updateSchedules(List<ScheduleRequest> requests, UserPrincipal currentUser);

    List<ProviderGalleryResponse> getProviderGallery(Long providerId);
    ProviderGalleryResponse addGalleryImage(AddGalleryImageRequest request, UserPrincipal currentUser);
    void deleteGalleryImage(Long id, UserPrincipal currentUser);

    void recordEvent(Long providerId, ProviderEventRequest request);
}

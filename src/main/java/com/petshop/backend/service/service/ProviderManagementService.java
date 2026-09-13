package com.petshop.backend.service.service;

import com.petshop.backend.dto.request.service.*;
import com.petshop.backend.dto.response.service.*;
import com.petshop.backend.security.UserPrincipal;

import java.util.List;

public interface ProviderManagementService {
    ProviderProfileResponse updateProviderProfile(UpdateProviderProfileRequest request, UserPrincipal currentUser);
    TeamMemberResponse addTeamMember(AddTeamMemberRequest request, UserPrincipal currentUser);
    TeamMemberResponse updateTeamMember(Long id, AddTeamMemberRequest request, UserPrincipal currentUser);
    void deleteTeamMember(Long id, UserPrincipal currentUser);
    List<ScheduleResponse> updateSchedules(List<ScheduleRequest> requests, UserPrincipal currentUser);
    List<ProviderGalleryResponse> getProviderGallery(Long providerId);
    ProviderGalleryResponse addGalleryImage(AddGalleryImageRequest request, UserPrincipal currentUser);
    void deleteGalleryImage(Long id, UserPrincipal currentUser);
}
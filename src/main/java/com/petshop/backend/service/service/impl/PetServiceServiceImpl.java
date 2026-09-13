package com.petshop.backend.service.service.impl;

import com.petshop.backend.dto.request.service.CreateServiceRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.ProviderProfileResponse;
import com.petshop.backend.dto.response.service.ScheduleResponse;
import com.petshop.backend.dto.response.service.ServiceResponse;
import com.petshop.backend.dto.response.service.TeamMemberResponse;
import com.petshop.backend.entity.service.PetService;
import com.petshop.backend.entity.service.Schedule;
import com.petshop.backend.entity.service.TeamMember;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.service.PetServiceRepository;
import com.petshop.backend.repository.service.ScheduleRepository;
import com.petshop.backend.repository.service.TeamMemberRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.service.PetServiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PetServiceServiceImpl implements PetServiceService {

    private final PetServiceRepository petServiceRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final TeamMemberRepository teamMemberRepository;

    public PetServiceServiceImpl(PetServiceRepository petServiceRepository,
                                 UserRepository userRepository,
                                 ScheduleRepository scheduleRepository,
                                 TeamMemberRepository teamMemberRepository) {
        this.petServiceRepository = petServiceRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ServiceResponse> getAllServices(String search, Long categoryId, Boolean emergency, @NonNull Pageable pageable) {
        Page<PetService> servicePage = petServiceRepository.findAll(pageable);
        List<ServiceResponse> content = servicePage.getContent().stream()
                .map(this::mapToServiceResponse)
                .toList();

        return PageResponse.<ServiceResponse>builder()
                .content(content)
                .pageNumber(servicePage.getNumber())
                .pageSize(servicePage.getSize())
                .totalElements(servicePage.getTotalElements())
                .totalPages(servicePage.getTotalPages())
                .last(servicePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse getServiceById(Long id) {
        PetService service = petServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PetService", "id", id));
        return mapToServiceResponse(service);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderProfileResponse getProviderProfile(Long providerId) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", providerId));

        List<Schedule> schedules = scheduleRepository.findByProviderId(providerId);
        List<TeamMember> teamMembers = teamMemberRepository.findByProviderId(providerId);

        List<ScheduleResponse> scheduleResponses = schedules.stream()
                .map(s -> ScheduleResponse.builder()
                        .id(s.getId())
                        .dayOfWeek(s.getDayOfWeek())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .isActive(s.getIsActive())
                        .build())
                .toList();

        List<TeamMemberResponse> teamMemberResponses = teamMembers.stream()
                .map(tm -> TeamMemberResponse.builder()
                        .id(tm.getId())
                        .fullName(tm.getFullName())
                        .role(tm.getRole())
                        .experience(tm.getExperience())
                        .note(tm.getNote())
                        .avatar(tm.getAvatar())
                        .build())
                .toList();

        return ProviderProfileResponse.builder()
                .providerId(provider.getId())
                .fullName(provider.getFullName())
                .phone(provider.getPhone())
                .email(provider.getEmail())
                .avatarUrl(provider.getAvatarUrl())
                .schedules(scheduleResponses)
                .teamMembers(teamMemberResponses)
                .build();
    }

    @Override
    @Transactional
    public ServiceResponse createService(CreateServiceRequest request, UserPrincipal currentUser) {
        PetService petService = PetService.builder()
                .providerId(currentUser.getId())
                .categoryId(request.getCategoryId())
                .title(request.getTitle())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .durationMinutes(request.getDurationMinutes())
                .address(request.getAddress())
                .amenities(request.getAmenities())
                .emergencyService(request.getEmergencyService() != null ? request.getEmergencyService() : false)
                .rating(BigDecimal.valueOf(5.0))
                .build();

        PetService saved = petServiceRepository.save(petService);
        return mapToServiceResponse(saved);
    }

    @Override
    @Transactional
    public ServiceResponse updateService(Long id, CreateServiceRequest request, UserPrincipal currentUser) {
        PetService petService = petServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PetService", "id", id));

        if (!petService.getProviderId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own pet services");
        }

        petService.setTitle(request.getTitle());
        petService.setDescription(request.getDescription());
        petService.setBasePrice(request.getBasePrice());
        petService.setDurationMinutes(request.getDurationMinutes());
        petService.setAddress(request.getAddress());
        petService.setAmenities(request.getAmenities());
        if (request.getEmergencyService() != null) {
            petService.setEmergencyService(request.getEmergencyService());
        }
        if (request.getCategoryId() != null) {
            petService.setCategoryId(request.getCategoryId());
        }

        PetService updated = petServiceRepository.save(petService);
        return mapToServiceResponse(updated);
    }

    @Override
    @Transactional
    public void deleteService(Long id, UserPrincipal currentUser) {
        PetService petService = petServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PetService", "id", id));

        if (!petService.getProviderId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own pet services");
        }

        petServiceRepository.delete(petService);
    }

    private ServiceResponse mapToServiceResponse(PetService service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getTitle())
                .description(service.getDescription())
                .price(service.getBasePrice())
                .durationMinutes(service.getDurationMinutes())
                .active(true)
                .categoryId(service.getCategoryId())
                .build();
    }
}
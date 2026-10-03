package com.petshop.backend.service.service.impl;

import com.petshop.backend.common.Roles;
import com.petshop.backend.common.ServiceType;
import com.petshop.backend.common.UserStatus;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.ServiceResponse;
import com.petshop.backend.entity.service.PetService;
import com.petshop.backend.entity.service.ServiceProviderProfile;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.service.PetServiceRepository;
import com.petshop.backend.service.service.PetServiceService;
import com.petshop.backend.service.service.ProviderSupport;
import com.petshop.backend.util.MaskUtils;
import com.petshop.backend.util.PageUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Public, read-only list of active services of approved providers. Providers manage theirs via the provider API. */
@Service
public class PetServiceServiceImpl implements PetServiceService {

    private final PetServiceRepository petServiceRepository;
    private final ProviderSupport support;

    public PetServiceServiceImpl(PetServiceRepository petServiceRepository, ProviderSupport support) {
        this.petServiceRepository = petServiceRepository;
        this.support = support;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ServiceResponse> getAllServices(String q, String serviceType, Long providerId, Boolean emergency,
                                                        int page, int size) {
        ServiceType type = ServiceType.parse(serviceType);
        Specification<PetService> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.or(cb.isNull(root.get("active")), cb.isTrue(root.get("active"))));
            Subquery<Long> approved = query.subquery(Long.class);
            var user = approved.from(User.class);
            approved.select(user.get("id")).where(
                    cb.equal(user.get("id"), root.get("providerId")),
                    cb.equal(user.get("status"), UserStatus.ACTIVE),
                    cb.equal(user.get("role"), Roles.ROLE_SERVICE_PROVIDER),
                    cb.isNull(user.get("deletedAt")));
            p.add(cb.exists(approved));
            if (providerId != null) p.add(cb.equal(root.get("providerId"), providerId));
            if (Boolean.TRUE.equals(emergency)) p.add(cb.isTrue(root.get("emergencyService")));
            if (type != null) {
                Subquery<Long> ofType = query.subquery(Long.class);
                var profile = ofType.from(ServiceProviderProfile.class);
                ofType.select(profile.get("providerId")).where(
                        cb.equal(profile.get("providerId"), root.get("providerId")),
                        cb.equal(cb.upper(profile.get("serviceType")), type.name()));
                p.add(cb.exists(ofType));
            }
            if (q != null && !q.isBlank()) {
                String pattern = "%" + MaskUtils.escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
                p.add(cb.or(cb.like(cb.lower(root.get("title")), pattern, '\\'),
                        cb.like(cb.lower(cb.coalesce(root.get("description"), "")), pattern, '\\')));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
        Page<PetService> result = petServiceRepository.findAll(spec, PageUtils.of(page, size, Sort.by("basePrice", "id")));
        return PageResponse.of(result, support::toService);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse getServiceById(Long id) {
        PetService service = petServiceRepository.findById(id)
                .filter(s -> !Boolean.FALSE.equals(s.getActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        return support.toService(service);
    }
}

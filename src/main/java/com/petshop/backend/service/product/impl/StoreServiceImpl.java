package com.petshop.backend.service.product.impl;

import com.petshop.backend.dto.request.product.CreateStoreRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.AccessDeniedCustomException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.StoreService;
import com.petshop.backend.util.MaskUtils;
import com.petshop.backend.util.PageUtils;
import com.petshop.backend.util.SlugUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public StoreServiceImpl(StoreRepository storeRepository, ProductRepository productRepository) {
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public StoreResponse createStore(CreateStoreRequest request, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        if (!currentUser.isAdmin() && storeRepository.existsByOwnerId(currentUser.getId())) {
            throw new ConflictException("STORE_EXISTS", "You already have a store; update it with PUT /api/v1/vendor/stores/me");
        }
        Store store = Store.builder()
                .ownerId(currentUser.getId())
                .slug(SlugUtils.unique(request.getName(), storeRepository::existsBySlug))
                .build();
        apply(store, request);
        return toResponse(storeRepository.save(store), true);
    }

    @Override
    @Transactional
    public StoreResponse updateStore(Long id, CreateStoreRequest request, UserPrincipal currentUser) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        CurrentUser.requireOwnerOrAdmin(currentUser, store.getOwnerId(), "store");
        apply(store, request);
        return toResponse(storeRepository.save(store), true);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse getStore(String idOrSlug) {
        return getStore(idOrSlug, null);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse getStore(String idOrSlug, UserPrincipal viewer) {
        Store store = (idOrSlug.matches("\\d+")
                ? storeRepository.findById(Long.parseLong(idOrSlug))
                : storeRepository.findBySlug(idOrSlug))
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", idOrSlug));
        boolean includePrivate = viewer != null && (viewer.isAdmin() || viewer.getId().equals(store.getOwnerId()));
        return toResponse(store, includePrivate);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse getMyStore(UserPrincipal currentUser) {
        return toResponse(myStore(currentUser), true);
    }

    @Override
    @Transactional
    public StoreResponse updateMyStore(CreateStoreRequest request, UserPrincipal currentUser) {
        Store store = myStore(currentUser);
        apply(store, request);
        return toResponse(storeRepository.save(store), true);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StoreResponse> getAllStores(String q, String city, int page, int size) {
        Specification<Store> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            // Stores of deleted accounts are not listed
            Subquery<Long> liveOwners = query.subquery(Long.class);
            Root<User> owner = liveOwners.from(User.class);
            liveOwners.select(owner.get("id")).where(cb.isNull(owner.get("deletedAt")));
            p.add(root.get("ownerId").in(liveOwners));
            if (q != null && !q.isBlank()) {
                p.add(cb.like(cb.lower(root.get("name")), "%" + MaskUtils.escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%", '\\'));
            }
            if (city != null && !city.isBlank()) {
                p.add(cb.equal(cb.lower(root.get("city")), city.trim().toLowerCase(Locale.ROOT)));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
        Page<Store> result = storeRepository.findAll(spec, PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "rating", "id")));
        return PageResponse.of(result, s -> toResponse(s, false));
    }

    @Override
    @Transactional
    public void deleteStore(Long id, UserPrincipal currentUser) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        CurrentUser.requireOwnerOrAdmin(currentUser, store.getOwnerId(), "store");
        if (productRepository.countByStoreIdAndDeletedAtIsNullAndIsActiveTrue(id) > 0) {
            throw new ConflictException("STORE_HAS_PRODUCTS", "Delete or hide the store's products first");
        }
        storeRepository.delete(store);
    }

    private Store myStore(UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        if (!currentUser.isVendor() && !currentUser.isAdmin()) {
            throw new AccessDeniedCustomException("Only vendors have a store");
        }
        return storeRepository.findFirstByOwnerIdOrderByIdAsc(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("You do not have a store yet"));
    }

    private void apply(Store store, CreateStoreRequest r) {
        if (!Objects.equals(store.getName(), r.getName().trim()) && store.getSlug() != null) {
            String currentSlug = store.getSlug();
            store.setSlug(SlugUtils.unique(r.getName(), slug -> !slug.equals(currentSlug) && storeRepository.existsBySlug(slug)));
        }
        store.setName(r.getName().trim());
        store.setTagline(blank(r.getTagline()));
        store.setDescription(blank(r.getDescription()));
        store.setLogoUrl(blank(r.getLogoUrl()));
        store.setBannerUrl(blank(r.getBannerUrl()));
        store.setPhone(blank(r.getPhone()));
        store.setAltPhone(blank(r.getAltPhone()));
        store.setWhatsapp(blank(r.getWhatsapp()));
        store.setInstagram(blank(r.getInstagram()));
        store.setEmail(blank(r.getEmail()));
        store.setAddress(blank(r.getAddress()));
        store.setCity(blank(r.getCity()));
        store.setLatitude(r.getLatitude());
        store.setLongitude(r.getLongitude());
        store.setWorkingHours(blank(r.getWorkingHours()));
        store.setDeliveryInfo(blank(r.getDeliveryInfo()));
        store.setFeatures(r.getFeatures() == null ? new ArrayList<>()
                : new ArrayList<>(r.getFeatures().stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).toList()));
        if (r.getVoen() != null) store.setVoen(blank(r.getVoen()));
    }

    private StoreResponse toResponse(Store s, boolean includePrivate) {
        return StoreResponse.builder()
                .id(s.getId())
                .ownerId(s.getOwnerId())
                .name(s.getName())
                .slug(s.getSlug())
                .tagline(s.getTagline())
                .logoUrl(s.getLogoUrl())
                .bannerUrl(s.getBannerUrl())
                .description(s.getDescription())
                .phone(s.getPhone())
                .altPhone(s.getAltPhone())
                .whatsapp(s.getWhatsapp())
                .instagram(s.getInstagram())
                .email(s.getEmail())
                .address(s.getAddress())
                .city(s.getCity())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .coords(s.getLatitude() == null || s.getLongitude() == null ? null
                        : new StoreResponse.Coords(s.getLatitude(), s.getLongitude()))
                .workingHours(s.getWorkingHours())
                .deliveryInfo(s.getDeliveryInfo())
                .features(s.getFeatures())
                .verified(Boolean.TRUE.equals(s.getVerified()))
                .badgeText(s.getBadgeText())
                .responseRate(s.getResponseRate())
                .rating(s.getRating())
                .reviewCount(s.getReviewCount() == null ? 0 : s.getReviewCount())
                .productCount(productRepository.countByStoreIdAndDeletedAtIsNullAndIsActiveTrue(s.getId()))
                .joinedDate(s.getCreatedAt())
                .voen(includePrivate ? s.getVoen() : null)
                .build();
    }

    private static String blank(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }
}

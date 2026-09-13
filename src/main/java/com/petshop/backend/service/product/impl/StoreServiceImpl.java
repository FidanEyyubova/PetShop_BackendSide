package com.petshop.backend.service.product.impl;

import com.petshop.backend.dto.request.product.CreateStoreRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.exception.AccessDeniedCustomException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.product.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional
    public StoreResponse createStore(CreateStoreRequest request, UserPrincipal currentUser) {
        String slug = request.getName().toLowerCase().replaceAll("[^a-z0-9]", "-") + "-" + System.currentTimeMillis();

        Store store = Store.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .logoUrl(request.getLogoUrl())
                .bannerUrl(request.getBannerUrl())
                .ownerId(currentUser.getId())
                .build();

        Store savedStore = storeRepository.save(store);
        return mapToStoreResponse(savedStore);
    }

    @Override
    @Transactional
    public StoreResponse updateStore(Long id, CreateStoreRequest request, UserPrincipal currentUser) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));

        if (!store.getOwnerId().equals(currentUser.getId()) && !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedCustomException("You do not have permission to edit this store");
        }

        store.setName(request.getName());
        store.setDescription(request.getDescription());
        store.setAddress(request.getAddress());
        store.setPhone(request.getPhone());
        store.setEmail(request.getEmail());
        store.setLogoUrl(request.getLogoUrl());
        store.setBannerUrl(request.getBannerUrl());

        Store updatedStore = storeRepository.save(store);
        return mapToStoreResponse(updatedStore);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        return mapToStoreResponse(store);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse getMyStore(UserPrincipal currentUser) {
        Store store = storeRepository.findByOwnerId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("You do not have a registered store profile yet"));
        return mapToStoreResponse(store);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StoreResponse> getAllStores(Pageable pageable) {
        Page<Store> storePage = storeRepository.findAll(pageable);
        List<StoreResponse> content = storePage.getContent().stream()
                .map(this::mapToStoreResponse)
                .toList();

        return PageResponse.<StoreResponse>builder()
                .content(content)
                .pageNumber(storePage.getNumber())
                .pageSize(storePage.getSize())
                .totalElements(storePage.getTotalElements())
                .totalPages(storePage.getTotalPages())
                .last(storePage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteStore(Long id, UserPrincipal currentUser) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));

        if (!store.getOwnerId().equals(currentUser.getId()) && !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedCustomException("You do not have permission to delete this store");
        }

        storeRepository.delete(store);
    }

    private StoreResponse mapToStoreResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .ownerId(store.getOwnerId())
                .name(store.getName())
                .slug(store.getSlug())
                .description(store.getDescription())
                .address(store.getAddress())
                .phone(store.getPhone())
                .email(store.getEmail())
                .logoUrl(store.getLogoUrl())
                .bannerUrl(store.getBannerUrl())
                .rating(store.getRating())
                .build();
    }
}
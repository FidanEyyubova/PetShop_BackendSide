package com.petshop.backend.service.product;

import com.petshop.backend.dto.request.product.CreateStoreRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.security.UserPrincipal;
import org.springframework.data.domain.Pageable;

public interface StoreService {

    StoreResponse createStore(CreateStoreRequest request, UserPrincipal currentUser);

    StoreResponse updateStore(Long id, CreateStoreRequest request, UserPrincipal currentUser);

    StoreResponse getStoreById(Long id);

    PageResponse<StoreResponse> getAllStores(Pageable pageable);

    void deleteStore(Long id, UserPrincipal currentUser);
}
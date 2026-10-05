package com.petshop.backend.service.product;

import com.petshop.backend.dto.request.product.CreateStoreRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.security.UserPrincipal;

public interface StoreService {
    StoreResponse createStore(CreateStoreRequest request, UserPrincipal currentUser);
    StoreResponse updateStore(Long id, CreateStoreRequest request, UserPrincipal currentUser);
    StoreResponse getStore(String idOrSlug);
    /** Same as getStore, but the owner and admins also get private fields (VÖEN). */
    StoreResponse getStore(String idOrSlug, UserPrincipal viewer);
    StoreResponse getMyStore(UserPrincipal currentUser);
    StoreResponse updateMyStore(CreateStoreRequest request, UserPrincipal currentUser);
    PageResponse<StoreResponse> getAllStores(String q, String city, int page, int size);
    void deleteStore(Long id, UserPrincipal currentUser);
}

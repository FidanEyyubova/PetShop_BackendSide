package com.petshop.backend.service.user;

import com.petshop.backend.dto.request.user.AddressRequest;
import com.petshop.backend.dto.response.user.UserAddressResponse;
import com.petshop.backend.security.UserPrincipal;
import java.util.List;

public interface AddressService {
    List<UserAddressResponse> getUserAddresses(UserPrincipal currentUser);
    UserAddressResponse createAddress(AddressRequest request, UserPrincipal currentUser);
    UserAddressResponse updateAddress(Long id, AddressRequest request, UserPrincipal currentUser);
    void deleteAddress(Long id, UserPrincipal currentUser);
    UserAddressResponse setDefaultAddress(Long id, UserPrincipal currentUser);
}
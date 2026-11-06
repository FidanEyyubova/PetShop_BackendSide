package com.petshop.backend.service.user.impl;

import com.petshop.backend.dto.request.user.AddressRequest;
import com.petshop.backend.dto.response.user.UserAddressResponse;
import com.petshop.backend.entity.user.UserAddress;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.user.UserAddressRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.user.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserAddressRepository addressRepository;

    public AddressServiceImpl(UserAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getUserAddresses(UserPrincipal currentUser) {
        return addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(CurrentUser.require(currentUser).getId())
                .stream().map(AddressServiceImpl::toResponse).toList();
    }

    @Override
    @Transactional
    public UserAddressResponse createAddress(AddressRequest request, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        boolean first = addressRepository.countByUserId(userId) == 0;
        boolean makeDefault = first || Boolean.TRUE.equals(request.getIsDefault());
        if (makeDefault) addressRepository.clearDefault(userId);

        UserAddress address = UserAddress.builder().userId(userId).isDefault(makeDefault).build();
        apply(address, request);
        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public UserAddressResponse updateAddress(Long id, AddressRequest request, UserPrincipal currentUser) {
        UserAddress address = find(id, currentUser);
        apply(address, request);
        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.clearDefault(address.getUserId());
            address.setIsDefault(true);
        }
        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long id, UserPrincipal currentUser) {
        UserAddress address = find(id, currentUser);
        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
        addressRepository.delete(address);
        addressRepository.flush();
        if (wasDefault) {
            addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(address.getUserId()).stream().findFirst()
                    .ifPresent(next -> {
                        next.setIsDefault(true);
                        addressRepository.save(next);
                    });
        }
    }

    @Override
    @Transactional
    public UserAddressResponse setDefaultAddress(Long id, UserPrincipal currentUser) {
        UserAddress address = find(id, currentUser);
        addressRepository.clearDefault(address.getUserId());
        address.setIsDefault(true);
        return toResponse(addressRepository.save(address));
    }

    private UserAddress find(Long id, UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        return addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
    }

    private static void apply(UserAddress address, AddressRequest r) {
        address.setTitle(r.getTitle().trim());
        address.setCity(r.getCity().trim());
        address.setDistrict(r.getDistrict().trim());
        address.setStreet(r.getStreet().trim());
        address.setFloor(blankToNull(r.getFloor()));
        address.setNote(blankToNull(r.getNote()));
        address.setFullAddress(r.getFullAddress() != null && !r.getFullAddress().isBlank()
                ? r.getFullAddress().trim()
                : Stream.of(r.getStreet(), r.getFloor(), r.getDistrict(), r.getCity())
                    .filter(s -> s != null && !s.isBlank()).map(String::trim).collect(Collectors.joining(", ")));
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    public static UserAddressResponse toResponse(UserAddress a) {
        return UserAddressResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .city(a.getCity())
                .district(a.getDistrict())
                .street(a.getStreet())
                .floor(a.getFloor())
                .note(a.getNote())
                .fullAddress(a.getFullAddress())
                .isDefault(Boolean.TRUE.equals(a.getIsDefault()))
                .build();
    }
}

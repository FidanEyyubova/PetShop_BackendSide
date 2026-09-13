package com.petshop.backend.service.user.impl;

import com.petshop.backend.dto.request.user.AddressRequest;
import com.petshop.backend.dto.response.user.UserAddressResponse;
import com.petshop.backend.entity.user.UserAddress;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.user.UserAddressRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.user.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserAddressRepository addressRepository;

    public AddressServiceImpl(UserAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getUserAddresses(UserPrincipal currentUser) {
        List<UserAddress> addresses = addressRepository.findByUserId(currentUser.getId());
        return addresses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserAddressResponse createAddress(AddressRequest request, UserPrincipal currentUser) {
        Long userId = currentUser.getId();

        boolean isFirst = addressRepository.findByUserId(userId).isEmpty();
        boolean setAsDefault = (request.getIsDefault() != null && request.getIsDefault()) || isFirst;

        if (setAsDefault) {
            clearPreviousDefault(userId);
        }

        UserAddress address = UserAddress.builder()
                .userId(userId)
                .title(request.getTitle() != null ? request.getTitle() : "Address")
                .fullAddress(request.getFullAddress())
                .city(request.getCity())
                .isDefault(setAsDefault)
                .build();

        UserAddress saved = addressRepository.save(address);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public UserAddressResponse updateAddress(Long id, AddressRequest request, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        UserAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        if (!address.getUserId().equals(userId)) {
            throw new BadRequestException("You can only update your own addresses");
        }

        if (request.getTitle() != null) {
            address.setTitle(request.getTitle());
        }
        if (request.getFullAddress() != null) {
            address.setFullAddress(request.getFullAddress());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getIsDefault() != null && request.getIsDefault()) {
            clearPreviousDefault(userId);
            address.setIsDefault(true);
        }

        UserAddress updated = addressRepository.save(address);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        UserAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        if (!address.getUserId().equals(userId)) {
            throw new BadRequestException("You can only delete your own addresses");
        }

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public UserAddressResponse setDefaultAddress(Long id, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        UserAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        if (!address.getUserId().equals(userId)) {
            throw new BadRequestException("You can only update your own addresses");
        }

        clearPreviousDefault(userId);
        address.setIsDefault(true);
        UserAddress updated = addressRepository.save(address);
        return mapToResponse(updated);
    }

    private void clearPreviousDefault(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });
    }

    private UserAddressResponse mapToResponse(UserAddress address) {
        return UserAddressResponse.builder()
                .id(address.getId())
                .title(address.getTitle())
                .fullAddress(address.getFullAddress())
                .city(address.getCity())
                .isDefault(address.getIsDefault() != null ? address.getIsDefault() : false)
                .build();
    }
}
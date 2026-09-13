package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.content.ContactInfoRequest;
import com.petshop.backend.dto.response.content.ContactInfoResponse;
import com.petshop.backend.entity.content.ContactInfo;
import com.petshop.backend.repository.content.ContactInfoRepository;
import com.petshop.backend.service.content.ContactInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactInfoServiceImpl implements ContactInfoService {

    private final ContactInfoRepository repository;

    public ContactInfoServiceImpl(ContactInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public ContactInfoResponse getContactInfo() {
        List<ContactInfo> list = repository.findAll();
        if (list.isEmpty()) {
            return new ContactInfoResponse(null, "", "", "", "", "", null);
        }
        ContactInfo info = list.get(0);
        return mapToResponse(info);
    }

    @Override
    public ContactInfoResponse saveOrUpdateContactInfo(ContactInfoRequest request) {
        List<ContactInfo> list = repository.findAll();
        ContactInfo info = list.isEmpty() ? new ContactInfo() : list.get(0);

        info.setLocation(request.getLocation());
        info.setMail(request.getMail());
        info.setPhone(request.getPhone());
        info.setWorkingHours(request.getWorkingHours());
        info.setLocationMapUrl(request.getLocationMapUrl());

        ContactInfo saved = repository.save(info);
        return mapToResponse(saved);
    }

    private ContactInfoResponse mapToResponse(ContactInfo info) {
        return new ContactInfoResponse(
                info.getId(),
                info.getLocation(),
                info.getMail(),
                info.getPhone(),
                info.getWorkingHours(),
                info.getLocationMapUrl(),
                info.getUpdatedAt()
        );
    }
}
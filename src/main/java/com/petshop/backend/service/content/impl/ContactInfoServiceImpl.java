package com.petshop.backend.service.content.impl;

import com.petshop.backend.i18n.Localized;
import com.petshop.backend.i18n.TranslatableFields;

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
    @org.springframework.cache.annotation.Cacheable(cacheNames = "contactInfo", key = "T(com.petshop.backend.common.Language).current()")
    public ContactInfoResponse getContactInfo() {
        List<ContactInfo> list = repository.findAll();
        if (list.isEmpty()) {
            return new ContactInfoResponse(null, "", "", "", "", "", null);
        }
        ContactInfo info = list.get(0);
        return mapToResponse(info);
    }

    @Override
    @org.springframework.cache.annotation.CacheEvict(cacheNames = "contactInfo", allEntries = true)
    public ContactInfoResponse saveOrUpdateContactInfo(ContactInfoRequest request) {
        List<ContactInfo> list = repository.findAll();
        ContactInfo info = list.isEmpty() ? new ContactInfo() : list.get(0);

        info.setLocation(request.getLocation());

        info.setTranslations(info.getTranslations().mergedWith(request.getTranslations(), TranslatableFields.CONTACT_INFO));
        info.setMail(request.getMail());
        info.setPhone(request.getPhone());
        info.setWorkingHours(request.getWorkingHours());
        info.setLocationMapUrl(request.getLocationMapUrl());
        info.setTranslations(info.getTranslations().mergedWith(request.getTranslations(), TranslatableFields.CONTACT_INFO));

        ContactInfo saved = repository.save(info);
        return mapToResponse(saved);
    }

    private ContactInfoResponse mapToResponse(ContactInfo info) {
        Localized t = Localized.of(info.getTranslations());
        ContactInfoResponse response = new ContactInfoResponse(
                info.getId(),
                t.text("location", info.getLocation()),
                info.getMail(),
                info.getPhone(),
                t.text("workingHours", info.getWorkingHours()),
                info.getLocationMapUrl(),
                info.getUpdatedAt()
        );
        response.setTranslations(Localized.editable(info.getTranslations(), "location", info.getLocation(), "workingHours", info.getWorkingHours()));
        return response;
    }
}
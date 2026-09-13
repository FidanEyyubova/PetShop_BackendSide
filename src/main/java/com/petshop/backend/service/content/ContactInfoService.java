package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.content.ContactInfoRequest;
import com.petshop.backend.dto.response.content.ContactInfoResponse;

public interface ContactInfoService {
    ContactInfoResponse getContactInfo();
    ContactInfoResponse saveOrUpdateContactInfo(ContactInfoRequest request);
}
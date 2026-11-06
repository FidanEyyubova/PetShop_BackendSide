package com.petshop.backend.dto.request.content;

import com.petshop.backend.i18n.TranslatableRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

public class ContactInfoRequest implements TranslatableRequest {
    private String location;
    private String mail;
    private String phone;
    private String workingHours;
    private String locationMapUrl;

    @Schema(description = TRANSLATIONS_DOC + " Fields: location, workingHours")
    private Map<String, Map<String, Object>> translations;

    public ContactInfoRequest() {}

    public ContactInfoRequest(String location, String mail, String phone, String workingHours, String locationMapUrl) {
        this.location = location;
        this.mail = mail;
        this.phone = phone;
        this.workingHours = workingHours;
        this.locationMapUrl = locationMapUrl;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    public String getLocationMapUrl() { return locationMapUrl; }
    public void setLocationMapUrl(String locationMapUrl) { this.locationMapUrl = locationMapUrl; }

    @Override
    public Map<String, Map<String, Object>> getTranslations() { return translations; }
    public void setTranslations(Map<String, Map<String, Object>> translations) { this.translations = translations; }
}

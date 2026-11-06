package com.petshop.backend.dto.request.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class NotificationPreferencesRequest {

    @JsonAlias("order")
    private Boolean orderUpdates;

    @JsonAlias({"promo", "promotions"})
    private Boolean promotions;

    @JsonAlias("blog")
    private Boolean blogUpdates;
}

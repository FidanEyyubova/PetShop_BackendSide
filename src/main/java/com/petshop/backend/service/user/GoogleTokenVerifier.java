package com.petshop.backend.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.petshop.backend.config.AppProperties;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.Set;

/**
 * Verifies a Google Sign-In ID token with Google's tokeninfo endpoint (Google checks the signature)
 * and makes sure it was issued for this application's client id.
 */
@Component
public class GoogleTokenVerifier {

    private static final Set<String> ISSUERS = Set.of("accounts.google.com", "https://accounts.google.com");

    private final RestClient restClient = RestClient.create("https://oauth2.googleapis.com");
    private final String clientId;

    public GoogleTokenVerifier(AppProperties properties) {
        this.clientId = properties.getGoogle().getClientId();
    }

    public record GoogleUser(String email, String name, String picture) {}

    public GoogleUser verify(String idToken) {
        if (clientId == null || clientId.isBlank()) {
            throw new BadRequestException("GOOGLE_LOGIN_DISABLED", "Google sign-in is not configured on the server (GOOGLE_CLIENT_ID)");
        }
        JsonNode info;
        try {
            info = restClient.get()
                    .uri(uri -> uri.path("/tokeninfo").queryParam("id_token", idToken).build())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException e) {
            throw new UnauthorizedException("INVALID_GOOGLE_TOKEN", "Google token is invalid or expired");
        }
        if (info == null
                || !clientId.equals(info.path("aud").asText())
                || !ISSUERS.contains(info.path("iss").asText())
                || !"true".equals(info.path("email_verified").asText())
                || info.path("exp").asLong(0) < Instant.now().getEpochSecond()) {
            throw new UnauthorizedException("INVALID_GOOGLE_TOKEN", "Google token is invalid or expired");
        }
        String email = info.path("email").asText();
        String name = info.path("name").asText(email);
        String picture = info.hasNonNull("picture") ? info.path("picture").asText() : null;
        return new GoogleUser(email.toLowerCase(), name, picture);
    }
}

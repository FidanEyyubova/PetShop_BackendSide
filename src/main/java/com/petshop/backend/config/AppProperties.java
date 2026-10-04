package com.petshop.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Application settings bound from the {@code app.*} section of application.yml / environment. */
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Cors cors = new Cors();
    private final Shipping shipping = new Shipping();
    private final Finance finance = new Finance();
    private final Storage storage = new Storage();
    private final Admin admin = new Admin();
    private final Jwt jwt = new Jwt();
    private final Mail mail = new Mail();
    private final Google google = new Google();
    private final RateLimit rateLimit = new RateLimit();
    private String timezone = "Asia/Baku";
    private String frontendUrl = "https://pet-shop-frontend-side.vercel.app";

    public Cors getCors() { return cors; }
    public Shipping getShipping() { return shipping; }
    public Finance getFinance() { return finance; }
    public Storage getStorage() { return storage; }
    public Admin getAdmin() { return admin; }
    public Jwt getJwt() { return jwt; }
    public Mail getMail() { return mail; }
    public Google getGoogle() { return google; }
    public RateLimit getRateLimit() { return rateLimit; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public String getFrontendUrl() { return frontendUrl; }
    public void setFrontendUrl(String frontendUrl) { this.frontendUrl = frontendUrl; }

    public static class Cors {
        /** Exact origins allowed to call the API with credentials. */
        private List<String> allowedOrigins = new ArrayList<>(List.of(
                "https://pet-shop-frontend-side.vercel.app",
                "http://localhost:5173",
                "http://localhost:3000"));

        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    }

    public static class Shipping {
        /** Orders at or above this subtotal ship for free ("Free shipping on orders over 50 ₼"). */
        private BigDecimal freeThreshold = new BigDecimal("50.00");
        /** Fee charged below the threshold. Business value not confirmed by the frontend; configurable. */
        private BigDecimal fee = new BigDecimal("5.00");
        /** Estimated delivery window, in hours from order creation. */
        private int deliveryHoursFrom = 24;
        private int deliveryHoursTo = 48;

        public BigDecimal getFreeThreshold() { return freeThreshold; }
        public void setFreeThreshold(BigDecimal freeThreshold) { this.freeThreshold = freeThreshold; }
        public BigDecimal getFee() { return fee; }
        public void setFee(BigDecimal fee) { this.fee = fee; }
        public int getDeliveryHoursFrom() { return deliveryHoursFrom; }
        public void setDeliveryHoursFrom(int deliveryHoursFrom) { this.deliveryHoursFrom = deliveryHoursFrom; }
        public int getDeliveryHoursTo() { return deliveryHoursTo; }
        public void setDeliveryHoursTo(int deliveryHoursTo) { this.deliveryHoursTo = deliveryHoursTo; }
    }

    public static class Finance {
        /** Platform commission taken from vendor revenue (0.10 = 10%). */
        private BigDecimal commissionRate = new BigDecimal("0.10");
        /** Products with stock at or below this value count as low stock. */
        private int lowStockThreshold = 5;

        public BigDecimal getCommissionRate() { return commissionRate; }
        public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
        public int getLowStockThreshold() { return lowStockThreshold; }
        public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    }

    public static class Storage {
        /** "local" (disk, served by /api/v1/files/{name}) or "s3" (any S3-compatible bucket). */
        private String type = "local";
        private String localDir = "uploads";
        /** Public base URL for files; empty = relative URLs served by this API. */
        private String publicBaseUrl = "";
        private long maxFileSizeBytes = 5 * 1024 * 1024;
        private final S3 s3 = new S3();

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getLocalDir() { return localDir; }
        public void setLocalDir(String localDir) { this.localDir = localDir; }
        public String getPublicBaseUrl() { return publicBaseUrl; }
        public void setPublicBaseUrl(String publicBaseUrl) { this.publicBaseUrl = publicBaseUrl; }
        public long getMaxFileSizeBytes() { return maxFileSizeBytes; }
        public void setMaxFileSizeBytes(long maxFileSizeBytes) { this.maxFileSizeBytes = maxFileSizeBytes; }
        public S3 getS3() { return s3; }

        public static class S3 {
            private String bucket;
            private String region = "auto";
            /** Custom endpoint for R2 / MinIO / Spaces; empty for AWS. */
            private String endpoint;
            private String accessKey;
            private String secretKey;

            public String getBucket() { return bucket; }
            public void setBucket(String bucket) { this.bucket = bucket; }
            public String getRegion() { return region; }
            public void setRegion(String region) { this.region = region; }
            public String getEndpoint() { return endpoint; }
            public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
            public String getAccessKey() { return accessKey; }
            public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
            public String getSecretKey() { return secretKey; }
            public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        }
    }

    public static class Admin {
        /** Seed admin account; created on startup only when both values are set. */
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class Jwt {
        /** HMAC secret, at least 64 characters. When missing a random secret is generated at startup. */
        private String secret;
        private long accessTokenMinutes = 60;
        private long refreshTokenDays = 30;
        /** Refresh token lifetime when the user did not tick "remember me". */
        private long shortRefreshTokenHours = 24;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getAccessTokenMinutes() { return accessTokenMinutes; }
        public void setAccessTokenMinutes(long accessTokenMinutes) { this.accessTokenMinutes = accessTokenMinutes; }
        public long getRefreshTokenDays() { return refreshTokenDays; }
        public void setRefreshTokenDays(long refreshTokenDays) { this.refreshTokenDays = refreshTokenDays; }
        public long getShortRefreshTokenHours() { return shortRefreshTokenHours; }
        public void setShortRefreshTokenHours(long shortRefreshTokenHours) { this.shortRefreshTokenHours = shortRefreshTokenHours; }
    }

    public static class Mail {
        private String from = "no-reply@petshop.az";
        /** Display name shown next to the sender address in the inbox. */
        private String fromName = "PetShop";
        /** Shop's own inbox; receives a notification for every new newsletter subscriber. Empty = no notification. */
        private String notifyTo;
        /** Brevo transactional-email API key. When set, mail is sent over HTTPS instead of SMTP (hosts such as Railway Hobby block SMTP ports). */
        private String brevoApiKey;

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getFromName() { return fromName; }
        public void setFromName(String fromName) { this.fromName = fromName; }
        public String getNotifyTo() { return notifyTo; }
        public void setNotifyTo(String notifyTo) { this.notifyTo = notifyTo; }
        public String getBrevoApiKey() { return brevoApiKey; }
        public void setBrevoApiKey(String brevoApiKey) { this.brevoApiKey = brevoApiKey; }
    }

    public static class Google {
        /** OAuth 2.0 Web client id from Google Cloud Console; empty disables Google sign-in. */
        private String clientId;

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
    }

    public static class RateLimit {
        private boolean enabled = true;
        /** Requests per minute per client IP for login, register and password reset. */
        private int authPerMinute = 10;
        /** Requests per minute per client IP for contact form and newsletter. */
        private int formsPerMinute = 5;
        /** Requests per minute per client IP for review creation. */
        private int reviewsPerMinute = 10;
        /** Requests per minute per client IP for provider analytics events. */
        private int eventsPerMinute = 60;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getAuthPerMinute() { return authPerMinute; }
        public void setAuthPerMinute(int authPerMinute) { this.authPerMinute = authPerMinute; }
        public int getFormsPerMinute() { return formsPerMinute; }
        public void setFormsPerMinute(int formsPerMinute) { this.formsPerMinute = formsPerMinute; }
        public int getReviewsPerMinute() { return reviewsPerMinute; }
        public void setReviewsPerMinute(int reviewsPerMinute) { this.reviewsPerMinute = reviewsPerMinute; }
        public int getEventsPerMinute() { return eventsPerMinute; }
        public void setEventsPerMinute(int eventsPerMinute) { this.eventsPerMinute = eventsPerMinute; }
    }
}

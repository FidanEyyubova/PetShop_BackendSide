package com.petshop.backend.integration;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Vendors and service providers start as PENDING_APPROVAL and can publish only after an admin approves them. */
class SellerApprovalIntegrationTest extends AbstractIntegrationTest {

    @Test
    void pendingVendorProductsStayHiddenUntilApproval() throws Exception {
        Response vendor = registerVendor(uniqueEmail("pendingvendor"));
        assertEquals(201, vendor.status(), vendor.toString());
        assertEquals("PENDING_APPROVAL", vendor.data().path("user").path("status").asText());
        String token = vendor.data().path("token").asText();
        long vendorId = vendor.data().path("user").path("id").asLong();

        Response product = post("/api/v1/vendor/products", Map.of(
                "title", "Hidden Leash", "brand", "PetBrand", "categoryId", createCategory(),
                "price", new BigDecimal("12.00"), "stockQuantity", 5), token);
        assertEquals(201, product.status(), product.toString());
        assertFalse(product.data().path("isActive").asBoolean());
        long productId = product.data().path("id").asLong();

        Response publish = patch("/api/v1/vendor/products/" + productId + "/status", Map.of("status", "active"), token);
        assertEquals(403, publish.status(), publish.toString());
        assertEquals("ACCOUNT_PENDING_APPROVAL", publish.code());

        Response pendingQueue = get("/api/v1/admin/users?role=VENDOR&status=PENDING_APPROVAL&size=100", adminToken());
        assertTrue(pendingQueue.data().path("content").findValues("id").stream().anyMatch(id -> id.asLong() == vendorId),
                "vendor should be in the approval queue");

        assertEquals(200, setUserStatus(vendorId, "ACTIVE").status());
        Response published = patch("/api/v1/vendor/products/" + productId + "/status", Map.of("status", "active"), token);
        assertEquals(200, published.status(), published.toString());
        assertTrue(published.data().path("isActive").asBoolean());
    }

    @Test
    void providerIsListedOnlyAfterApproval() throws Exception {
        Response provider = registerProvider(uniqueEmail("provider"));
        assertEquals(201, provider.status(), provider.toString());
        String token = provider.data().path("token").asText();
        long providerId = provider.data().path("user").path("id").asLong();

        Response service = post("/api/v1/pet-services/provider/services",
                Map.of("title", "Checkup", "priceMin", new BigDecimal("20.00"), "active", true), token);
        assertEquals(201, service.status(), service.toString());
        assertFalse(service.data().path("active").asBoolean(), "pending provider's service must be saved hidden");
        long serviceId = service.data().path("id").asLong();

        Response activate = patch("/api/v1/pet-services/provider/services/" + serviceId + "/status", Map.of("active", true), token);
        assertEquals(403, activate.status(), activate.toString());
        assertEquals(404, get("/api/v1/providers/" + providerId, null).status());

        assertEquals(200, setUserStatus(providerId, "ACTIVE").status());
        assertEquals(200, patch("/api/v1/pet-services/provider/services/" + serviceId + "/status", Map.of("active", true), token).status());
        assertEquals(200, get("/api/v1/providers/" + providerId, null).status());
    }

    @Test
    void suspensionSignsOutAndHidesTheSeller() throws Exception {
        Response provider = registerProvider(uniqueEmail("suspended"));
        long providerId = provider.data().path("user").path("id").asLong();
        String token = provider.data().path("token").asText();
        setUserStatus(providerId, "ACTIVE");
        assertEquals(200, get("/api/v1/providers/" + providerId, null).status());

        Response suspended = setUserStatus(providerId, "SUSPENDED");
        assertEquals(200, suspended.status(), suspended.toString());
        assertEquals(404, get("/api/v1/providers/" + providerId, null).status());

        Response refresh = post("/api/v1/auth/refresh-token",
                Map.of("refreshToken", provider.data().path("refreshToken").asText()), null);
        assertEquals(401, refresh.status(), refresh.toString());
        assertNotEquals(200, get("/api/v1/pet-services/provider/services", token).status());
    }

    @Test
    void adminAccountsCannotBeChangedThroughStatusEndpoint() throws Exception {
        String admin = adminToken();
        long adminId = get("/api/v1/auth/me", admin).data().path("id").asLong();

        Response response = setUserStatus(adminId, "SUSPENDED");
        assertEquals(409, response.status(), response.toString());
    }
}

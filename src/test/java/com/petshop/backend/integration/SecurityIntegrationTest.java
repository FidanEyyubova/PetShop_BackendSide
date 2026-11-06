package com.petshop.backend.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityIntegrationTest extends AbstractIntegrationTest {

    @Test
    void storefrontIsPublic() throws Exception {
        assertEquals(200, get("/api/v1/products", null).status());
        assertEquals(200, get("/api/v1/categories", null).status());
        assertEquals(200, get("/actuator/health", null).status());
        assertEquals(200, get("/actuator/health/liveness", null).status());
        assertEquals(200, get("/actuator/health/readiness", null).status());
    }

    @Test
    void signedInAreasRequireAToken() throws Exception {
        Response response = get("/api/v1/orders", null);
        assertEquals(401, response.status(), response.toString());
        assertEquals(401, get("/api/v1/cart", null).status());
    }

    @Test
    void invalidTokenIsRejected() throws Exception {
        assertEquals(401, get("/api/v1/auth/me", "not.a.valid.token").status());
    }

    @Test
    void buyerCannotUseAdminOrVendorEndpoints() throws Exception {
        String buyer = registerBuyer(uniqueEmail("sec")).data().path("token").asText();

        Response admin = get("/api/v1/admin/users", buyer);
        assertEquals(403, admin.status(), admin.toString());
        assertEquals(403, get("/api/v1/vendor/products", buyer).status());
    }

    @Test
    void adminCanUseAdminEndpoints() throws Exception {
        Response response = get("/api/v1/admin/users", adminToken());
        assertEquals(200, response.status(), response.toString());
    }
}

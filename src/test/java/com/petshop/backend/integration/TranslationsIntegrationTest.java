package com.petshop.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TranslationsIntegrationTest extends AbstractIntegrationTest {

    private String vendorToken;
    private long categoryId;

    @BeforeEach
    void approvedVendor() throws Exception {
        Response category = post("/api/v1/admin/categories", Map.of(
                "title", "Yem " + System.nanoTime(),
                "translations", Map.of("en", Map.of("title", "Food"), "ru", Map.of("title", "Корм"))), adminToken());
        assertEquals(201, category.status(), category.toString());
        categoryId = category.data().path("id").asLong();

        Response vendor = registerVendor(uniqueEmail("i18n.vendor"));
        assertEquals(201, vendor.status(), vendor.toString());
        assertEquals(200, setUserStatus(vendor.data().path("user").path("id").asLong(), "ACTIVE").status());
        vendorToken = vendor.data().path("token").asText();
    }

    @Test
    void productTextFollowsTheRequestedLanguage() throws Exception {
        String unique = "Sputnik" + System.nanoTime();
        Response created = post("/api/v1/vendor/products", Map.of(
                "title", "İt yemi", "brand", "PetBrand", "categoryId", categoryId,
                "price", new BigDecimal("20.00"), "stockQuantity", 5,
                "description", "Təsvir", "keyBenefits", List.of("Sağlam"),
                "translations", Map.of(
                        "en", Map.of("title", "Dog food", "keyBenefits", List.of("Healthy")),
                        "ru", Map.of("title", "Корм " + unique, "description", "Описание"))), vendorToken);
        assertEquals(201, created.status(), created.toString());
        long id = created.data().path("id").asLong();

        JsonNode az = get("/api/v1/products/" + id, null).data();
        assertEquals("İt yemi", az.path("title").asText());
        assertEquals("Yem", az.path("categoryName").asText().substring(0, 3));
        assertEquals("Dog food", az.path("translations").path("en").path("title").asText());
        assertEquals("İt yemi", az.path("translations").path("az").path("title").asText());

        JsonNode en = get("/api/v1/products/" + id + "?lang=en", null).data();
        assertEquals("Dog food", en.path("title").asText());
        assertEquals("Healthy", en.path("keyBenefits").get(0).asText());
        assertEquals("Təsvir", en.path("description").asText());
        assertEquals("Food", en.path("categoryName").asText());

        JsonNode ru = get("/api/v1/products/" + id + "?lang=ru", null).data();
        assertEquals("Корм " + unique, ru.path("title").asText());
        assertEquals("Описание", ru.path("description").asText());
        assertEquals("Корм", ru.path("categoryName").asText());

        String q = URLEncoder.encode(unique.toLowerCase(), StandardCharsets.UTF_8);
        JsonNode found = get("/api/v1/products?q=" + q + "&lang=ru", null).data();
        assertEquals(1, found.path("content").size(), found.toString());
        assertEquals("Корм " + unique, found.path("content").get(0).path("title").asText());

        Response updated = put("/api/v1/vendor/products/" + id, Map.of(
                "title", "İt yemi 2", "brand", "PetBrand", "categoryId", categoryId,
                "price", new BigDecimal("20.00"), "stockQuantity", 5,
                "translations", Map.of("en", Map.of("title", "Dog food 2"))), vendorToken);
        assertEquals(200, updated.status(), updated.toString());
        assertEquals("Dog food 2", updated.data().path("translations").path("en").path("title").asText());
        assertEquals("Корм " + unique, updated.data().path("translations").path("ru").path("title").asText());
    }

    @Test
    void errorMessagesFollowTheRequestedLanguage() throws Exception {
        Response ru = get("/api/v1/products/987654321?lang=ru", null);
        assertEquals(404, ru.status());
        assertEquals("Товар: не найдено (id = '987654321')", ru.body().path("message").asText());

        Response az = post("/api/v1/vendor/products", Map.of("brand", "X"), vendorToken);
        assertEquals(400, az.status());
        assertEquals("Məlumatlarda xəta var", az.body().path("message").asText());
        assertEquals("Məhsulun adı tələb olunur", az.data().path("title").asText());
    }
}

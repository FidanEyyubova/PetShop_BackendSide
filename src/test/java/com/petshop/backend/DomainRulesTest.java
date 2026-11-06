package com.petshop.backend;

import com.petshop.backend.common.*;
import com.petshop.backend.util.JsonUtils;
import com.petshop.backend.util.MaskUtils;
import com.petshop.backend.util.MoneyUtils;
import com.petshop.backend.util.SlugUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DomainRulesTest {

    @Test
    void orderStatusTransitions() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.PROCESSING));
        assertTrue(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.SHIPPING));
        assertTrue(OrderStatus.SHIPPING.canTransitionTo(OrderStatus.DELIVERED));
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.DELIVERED));
        assertFalse(OrderStatus.SHIPPING.canTransitionTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CANCELLED));
        assertTrue(OrderStatus.PROCESSING.isCancellableByBuyer());
        assertFalse(OrderStatus.SHIPPING.isCancellableByBuyer());
    }

    @Test
    void orderStatusAliases() {
        assertEquals(OrderStatus.PENDING, OrderStatus.parse("new"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.parse("completed"));
        assertEquals(OrderStatus.SHIPPING, OrderStatus.parse("Shipping"));
        assertNull(OrderStatus.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.parse("lost"));
    }

    @Test
    void effectivePriceAndDiscount() {
        assertEquals(new BigDecimal("39.90"), MoneyUtils.effectivePrice(new BigDecimal("45.90"), new BigDecimal("39.90")));
        assertEquals(new BigDecimal("45.90"), MoneyUtils.effectivePrice(new BigDecimal("45.90"), new BigDecimal("50.00")));
        assertEquals(new BigDecimal("45.90"), MoneyUtils.effectivePrice(new BigDecimal("45.90"), null));
        assertEquals(13, MoneyUtils.discountPercent(new BigDecimal("45.90"), new BigDecimal("39.90")));
        assertEquals(0, MoneyUtils.discountPercent(new BigDecimal("45.90"), null));
    }

    @Test
    void petTypeNormalisation() {
        assertEquals("dogs", PetType.normalizeId("Dog"));
        assertEquals("small-pets", PetType.normalizeId("rodent"));
        assertEquals("small-pets", PetType.normalizeId("small_pets"));
        assertNull(PetType.normalizeId(""));
        assertThrows(IllegalArgumentException.class, () -> PetType.parse("dragon"));
    }

    @Test
    void roleNormalisation() {
        assertEquals(Roles.ROLE_ADMIN, Roles.normalize("ROLE_SUPER_ADMIN"));
        assertEquals(Roles.ROLE_VENDOR, Roles.normalize("seller"));
        assertEquals(Roles.ROLE_SERVICE_PROVIDER, Roles.normalize("service"));
        assertEquals(Roles.ROLE_BUYER, Roles.normalize(null));
    }

    @Test
    void enumAliases() {
        assertEquals(PaymentMethod.CASH_ON_DELIVERY, PaymentMethod.parse("cashOnDelivery"));
        assertEquals(PaymentMethod.CARD_ONLINE, PaymentMethod.parse("online"));
        assertEquals(ReviewTargetType.SERVICE_PROVIDER, ReviewTargetType.parse("serviceCenter"));
        assertEquals(ProductSort.PRICE_LOW, ProductSort.parse("price-low"));
        assertEquals(ServiceType.VETERINARY, ServiceType.parse("vet"));
        assertThrows(IllegalArgumentException.class, () -> ProductSort.parse("drop table"));
    }

    @Test
    void slugs() {
        assertEquals("qida-ve-qidalanma", SlugUtils.slugify("Qida və Qidalanma"));
        assertEquals("pisik-oyuncaqlari", SlugUtils.slugify("Pişik oyuncaqları!"));
        Set<String> taken = Set.of("pet-land", "pet-land-2");
        assertEquals("pet-land-3", SlugUtils.unique("Pet Land", taken::contains));
    }

    @Test
    void tolerantListParsing() {
        assertEquals(List.of("a", "b"), JsonUtils.readStringList("[\"a\",\"b\"]"));
        assertEquals(List.of("2 kg", "4 kg"), JsonUtils.readStringList("2 kg, 4 kg"));
        assertTrue(JsonUtils.readStringList(null).isEmpty());
    }

    @Test
    void ibanMasking() {
        assertEquals("AZ21 **** **** 1944", MaskUtils.maskIban("AZ21NABZ00000000137010001944"));
        assertEquals("100\\%", MaskUtils.escapeLike("100%"));
    }

    @Test
    void validationPatterns() {
        assertTrue("Secret123".matches(ValidationPatterns.PASSWORD));
        assertFalse("secret".matches(ValidationPatterns.PASSWORD));
        assertTrue("AZ21NABZ00000000137010001944".matches(ValidationPatterns.IBAN));
        assertTrue("1234567890".matches(ValidationPatterns.VOEN));
        assertTrue("+994 50 123 45 67".matches(ValidationPatterns.PHONE));
        assertFalse("data:image/png;base64,AAA".matches(ValidationPatterns.IMAGE_URL));
        assertTrue("/api/v1/files/abc.png".matches(ValidationPatterns.IMAGE_URL));
    }
}

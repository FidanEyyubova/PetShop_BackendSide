package com.petshop.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The main marketplace flow: an approved vendor lists a product, a buyer checks out from the cart,
 * stock is reserved and the vendor moves the order through its lifecycle.
 */
class CheckoutFlowIntegrationTest extends AbstractIntegrationTest {

    private String vendorToken;
    private String buyerToken;
    private long categoryId;

    @BeforeEach
    void approvedVendorAndBuyer() throws Exception {
        categoryId = createCategory();
        Response vendor = registerVendor(uniqueEmail("vendor"));
        assertEquals(201, vendor.status(), vendor.toString());
        long vendorId = vendor.data().path("user").path("id").asLong();
        assertEquals(200, setUserStatus(vendorId, "ACTIVE").status());
        vendorToken = vendor.data().path("token").asText();

        buyerToken = registerBuyer(uniqueEmail("shopper")).data().path("token").asText();
    }

    @Test
    void checkoutReservesStockAndVendorCompletesTheOrder() throws Exception {
        long productId = createProduct("Dog Food", "20.00", 10);

        Response added = post("/api/v1/cart/items", Map.of("productId", productId, "quantity", 3), buyerToken);
        assertTrue(added.status() == 200 || added.status() == 201, added.toString());

        Response checkout = post("/api/v1/orders", Map.of("shippingAddress", "Baku, Nizami 1"), buyerToken);
        assertEquals(201, checkout.status(), checkout.toString());
        JsonNode order = checkout.data().path("orders").get(0);
        assertEquals("PENDING", order.path("status").asText().toUpperCase());
        // 3 x 20.00 = 60.00, above the free-shipping threshold.
        assertEquals(0, new BigDecimal("60.00").compareTo(checkout.data().path("subtotal").decimalValue()));
        assertEquals(0, BigDecimal.ZERO.compareTo(checkout.data().path("shippingFee").decimalValue()));
        assertEquals(7, stockOf(productId));

        Response cart = get("/api/v1/cart", buyerToken);
        assertEquals(200, cart.status());
        assertTrue(cart.toString().contains("\"items\":[]") || cart.data().path("items").isEmpty(), "cart should be empty: " + cart);

        long orderId = order.path("id").asLong();
        assertEquals(200, setOrderStatus(orderId, "PROCESSING").status());

        Response backwards = setOrderStatus(orderId, "PENDING");
        assertEquals(400, backwards.status(), backwards.toString());
        assertEquals("INVALID_STATUS_TRANSITION", backwards.code());

        assertEquals(200, setOrderStatus(orderId, "SHIPPING").status());
        Response delivered = setOrderStatus(orderId, "DELIVERED");
        assertEquals(200, delivered.status(), delivered.toString());

        Response buyerView = get("/api/v1/orders/" + orderId, buyerToken);
        assertEquals("DELIVERED", buyerView.data().path("status").asText().toUpperCase());
    }

    @Test
    void orderingMoreThanTheStockIsRejected() throws Exception {
        long productId = createProduct("Cat Toy", "5.00", 2);

        Response response = post("/api/v1/orders", Map.of(
                "shippingAddress", "Baku", "items", new Object[]{Map.of("productId", productId, "quantity", 5)}), buyerToken);

        assertEquals(409, response.status(), response.toString());
        assertEquals("STOCK_PROBLEM", response.code());
        assertEquals(2, stockOf(productId));
    }

    @Test
    void cancellingAnOrderReturnsTheStock() throws Exception {
        long productId = createProduct("Bird Seed", "8.00", 4);

        Response checkout = post("/api/v1/orders", Map.of(
                "shippingAddress", "Baku", "items", new Object[]{Map.of("productId", productId, "quantity", 4)}), buyerToken);
        assertEquals(201, checkout.status(), checkout.toString());
        assertEquals(0, stockOf(productId));

        long orderId = checkout.data().path("orders").get(0).path("id").asLong();
        Response cancelled = put("/api/v1/orders/" + orderId + "/cancel", Map.of("reason", "Changed my mind"), buyerToken);
        assertEquals(200, cancelled.status(), cancelled.toString());
        assertEquals(4, stockOf(productId));
    }

    @Test
    void emptyCartCannotBeCheckedOut() throws Exception {
        Response response = post("/api/v1/orders", Map.of("shippingAddress", "Baku"), buyerToken);
        assertEquals(400, response.status(), response.toString());
        assertEquals("EMPTY_CART", response.code());
    }

    @Test
    void vendorCannotChangeAnotherVendorsOrder() throws Exception {
        long productId = createProduct("Fish Tank", "30.00", 1);
        Response checkout = post("/api/v1/orders", Map.of(
                "shippingAddress", "Baku", "items", new Object[]{Map.of("productId", productId, "quantity", 1)}), buyerToken);
        long orderId = checkout.data().path("orders").get(0).path("id").asLong();

        Response other = registerVendor(uniqueEmail("othervendor"));
        setUserStatus(other.data().path("user").path("id").asLong(), "ACTIVE");
        Response response = put("/api/v1/vendor/orders/" + orderId + "/status", Map.of("status", "PROCESSING"),
                other.data().path("token").asText());

        assertTrue(response.status() == 403 || response.status() == 404, response.toString());
    }

    private long createProduct(String title, String price, int stock) throws Exception {
        Response product = post("/api/v1/vendor/products", Map.of(
                "title", title, "brand", "PetBrand", "categoryId", categoryId,
                "price", new BigDecimal(price), "stockQuantity", stock), vendorToken);
        assertEquals(201, product.status(), product.toString());
        assertTrue(product.data().path("isActive").asBoolean(), "approved vendor's product should be active: " + product);
        return product.data().path("id").asLong();
    }

    private int stockOf(long productId) throws Exception {
        Response product = get("/api/v1/vendor/products/" + productId, vendorToken);
        assertEquals(200, product.status(), product.toString());
        return product.data().path("stockQuantity").asInt();
    }

    private Response setOrderStatus(long orderId, String status) throws Exception {
        return put("/api/v1/vendor/orders/" + orderId + "/status", Map.of("status", status), vendorToken);
    }
}

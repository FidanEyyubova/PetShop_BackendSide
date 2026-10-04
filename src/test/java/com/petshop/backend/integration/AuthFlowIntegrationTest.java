package com.petshop.backend.integration;

import com.petshop.backend.repository.user.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Registration, login, token refresh with rotation, validation errors and the password reset flow. */
class AuthFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PasswordResetTokenRepository resetTokens;

    @Test
    void registerLoginAndReadProfile() throws Exception {
        String email = uniqueEmail("buyer");
        Response registered = registerBuyer(email);
        assertEquals(201, registered.status(), registered.toString());
        assertEquals("ROLE_BUYER", registered.data().path("role").asText());
        assertFalse(registered.data().path("token").asText().isBlank());

        String token = login(email, PASSWORD);
        Response me = get("/api/v1/auth/me", token);
        assertEquals(200, me.status(), me.toString());
        assertEquals(email, me.data().path("email").asText());
        assertEquals("ACTIVE", me.data().path("status").asText());
    }

    @Test
    void duplicateEmailIsRejected() throws Exception {
        String email = uniqueEmail("dup");
        assertEquals(201, registerBuyer(email).status());

        Response again = registerBuyer(email);
        assertEquals(409, again.status(), again.toString());
        assertFalse(again.body().path("success").asBoolean());
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        String email = uniqueEmail("wrongpwd");
        registerBuyer(email);

        Response response = post("/api/v1/auth/login", Map.of("email", email, "password", "Wrong12345"), null);
        assertEquals(401, response.status(), response.toString());
    }

    @Test
    void invalidInputReturnsFieldErrors() throws Exception {
        Response response = post("/api/v1/auth/register", Map.of(
                "fullName", "X", "email", "not-an-email", "password", "short", "confirmPassword", "short"), null);

        assertEquals(400, response.status(), response.toString());
        assertEquals("VALIDATION_ERROR", response.code());
        assertTrue(response.data().has("email"), "email error expected: " + response);
        assertTrue(response.data().has("password"), "password error expected: " + response);
    }

    @Test
    void refreshTokenIsRotatedAndReuseIsDetected() throws Exception {
        Response registered = registerBuyer(uniqueEmail("refresh"));
        String firstRefresh = registered.data().path("refreshToken").asText();

        Response refreshed = post("/api/v1/auth/refresh-token", Map.of("refreshToken", firstRefresh), null);
        assertEquals(200, refreshed.status(), refreshed.toString());
        String secondRefresh = refreshed.data().path("refreshToken").asText();
        assertNotEquals(firstRefresh, secondRefresh);

        // The old token was rotated away: using it again is treated as theft.
        Response reused = post("/api/v1/auth/refresh-token", Map.of("refreshToken", firstRefresh), null);
        assertEquals(401, reused.status(), reused.toString());
    }

    @Test
    void passwordResetWithEmailedCode() throws Exception {
        String email = uniqueEmail("reset");
        registerBuyer(email);

        assertEquals(200, post("/api/v1/auth/forgot-password", Map.of("email", email), null).status());
        String code = resetTokens.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email).orElseThrow().getCode();

        Response wrongCode = post("/api/v1/auth/reset-password", Map.of(
                "email", email, "code", code.equals("000000") ? "111111" : "000000",
                "newPassword", "NewParol123", "confirmPassword", "NewParol123"), null);
        assertEquals(400, wrongCode.status(), wrongCode.toString());

        Response reset = post("/api/v1/auth/reset-password", Map.of(
                "email", email, "code", code, "newPassword", "NewParol123", "confirmPassword", "NewParol123"), null);
        assertEquals(200, reset.status(), reset.toString());

        assertEquals(401, post("/api/v1/auth/login", Map.of("email", email, "password", PASSWORD), null).status());
        assertFalse(login(email, "NewParol123").isBlank());
    }

    @Test
    void forgotPasswordDoesNotRevealUnknownEmails() throws Exception {
        Response response = post("/api/v1/auth/forgot-password", Map.of("email", uniqueEmail("nobody")), null);
        assertEquals(200, response.status(), response.toString());
    }
}

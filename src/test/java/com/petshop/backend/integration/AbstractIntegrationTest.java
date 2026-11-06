package com.petshop.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    protected static final String ADMIN_EMAIL = "it.admin@petshop.test";
    protected static final String ADMIN_PASSWORD = "AdminPass123";
    protected static final String PASSWORD = "Parol12345";

    private static final String DB_URL;
    private static final String DB_USERNAME;
    private static final String DB_PASSWORD;
    private static final AtomicLong SEQUENCE = new AtomicLong(System.currentTimeMillis());

    static {
        if (DockerClientFactory.instance().isDockerAvailable()) {
            PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
            postgres.start();
            DB_URL = postgres.getJdbcUrl();
            DB_USERNAME = postgres.getUsername();
            DB_PASSWORD = postgres.getPassword();
        } else {
            DB_URL = env("TEST_DB_URL", "jdbc:postgresql://localhost:5432/petshop_test");
            DB_USERNAME = env("TEST_DB_USERNAME", "postgres");
            DB_PASSWORD = env("TEST_DB_PASSWORD", "postgres");
            createLocalDatabaseIfMissing();
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> DB_URL);
        registry.add("spring.datasource.username", () -> DB_USERNAME);
        registry.add("spring.datasource.password", () -> DB_PASSWORD);
        registry.add("app.admin.email", () -> ADMIN_EMAIL);
        registry.add("app.admin.password", () -> ADMIN_PASSWORD);
        registry.add("app.rate-limit.enabled", () -> "false");
        registry.add("app.storage.local-dir", () -> "target/test-uploads");
        registry.add("spring.mail.host", () -> "");
        registry.add("app.google.client-id", () -> "");
    }

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper json;

    protected Response get(String url, String token) throws Exception {
        return perform(MockMvcRequestBuilders.get(url), null, token);
    }

    protected Response post(String url, Object body, String token) throws Exception {
        return perform(MockMvcRequestBuilders.post(url), body, token);
    }

    protected Response put(String url, Object body, String token) throws Exception {
        return perform(MockMvcRequestBuilders.put(url), body, token);
    }

    protected Response patch(String url, Object body, String token) throws Exception {
        return perform(MockMvcRequestBuilders.patch(url), body, token);
    }

    private Response perform(MockHttpServletRequestBuilder request, Object body, String token) throws Exception {
        request.accept(MediaType.APPLICATION_JSON);
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body));
        }
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        MvcResult result = mvc.perform(request).andReturn();
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return new Response(result.getResponse().getStatus(), content.isBlank() ? json.nullNode() : json.readTree(content));
    }

    protected record Response(int status, JsonNode body) {
        public JsonNode data() {
            return body.path("data");
        }

        public String code() {
            return body.path("code").asText(null);
        }

        @Override
        public String toString() {
            return status + " " + body;
        }
    }

    protected static String uniqueEmail(String prefix) {
        return prefix + "." + SEQUENCE.incrementAndGet() + "@petshop.test";
    }

    protected Response registerBuyer(String email) throws Exception {
        return post("/api/v1/auth/register", Map.of(
                "fullName", "Test Buyer", "email", email,
                "password", PASSWORD, "confirmPassword", PASSWORD, "agreeTerms", true), null);
    }

    protected Response registerVendor(String email) throws Exception {
        return post("/api/v1/auth/register/vendor", Map.of(
                "fullName", "Test Vendor", "email", email, "phone", "+994501234567",
                "password", PASSWORD, "confirmPassword", PASSWORD, "agreeTerms", true,
                "storeName", "Store " + SEQUENCE.incrementAndGet()), null);
    }

    protected Response registerProvider(String email) throws Exception {
        return post("/api/v1/auth/register/provider", Map.of(
                "fullName", "Test Provider", "centerName", "Center " + SEQUENCE.incrementAndGet(),
                "serviceType", "VETERINARY", "email", email, "phone", "+994501234567",
                "password", PASSWORD, "confirmPassword", PASSWORD, "agreeTerms", true), null);
    }

    protected String login(String email, String password) throws Exception {
        Response response = post("/api/v1/auth/login", Map.of("email", email, "password", password), null);
        if (response.status() != 200) throw new AssertionError("Login failed: " + response);
        return response.data().path("token").asText();
    }

    protected String adminToken() throws Exception {
        return login(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    protected long createCategory() throws Exception {
        Response category = post("/api/v1/admin/categories",
                Map.of("title", "Category " + SEQUENCE.incrementAndGet()), adminToken());
        if (category.status() != 201 && category.status() != 200) throw new AssertionError("Category not created: " + category);
        return category.data().path("id").asLong();
    }

    protected Response setUserStatus(long userId, String status) throws Exception {
        return patch("/api/v1/admin/users/" + userId + "/status", Map.of("status", status), adminToken());
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static void createLocalDatabaseIfMissing() {
        int slash = DB_URL.lastIndexOf('/');
        String database = DB_URL.substring(slash + 1).split("\\?")[0];
        String serverUrl = DB_URL.substring(0, slash) + "/postgres";
        try (Connection connection = DriverManager.getConnection(serverUrl, DB_USERNAME, DB_PASSWORD);
             var check = connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
            check.setString(1, database);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next()) {
                    try (var create = connection.createStatement()) {
                        create.execute("CREATE DATABASE \"" + database.replace("\"", "") + "\"");
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Docker is not available and the local test database " + DB_URL
                    + " could not be prepared. Start PostgreSQL or set TEST_DB_URL / TEST_DB_USERNAME / TEST_DB_PASSWORD.", e);
        }
    }
}

package com.petshop.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Swagger UI at /swagger-ui.html. Endpoints are grouped by audience (dropdown at the top right).
 * Public endpoints are detected with the same rules as SecurityConfig and shown without the lock icon.
 */
@Configuration
public class OpenApiConfig {

    public static final String BEARER = "bearerAuth";

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    /** Keep in sync with SecurityConfig. */
    private static final List<String> PUBLIC_ANY = List.of(
            "/api/v1/auth/login", "/api/v1/auth/google", "/api/v1/auth/register", "/api/v1/auth/register/vendor", "/api/v1/auth/register/provider",
            "/api/v1/auth/refresh-token", "/api/v1/auth/logout", "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password",
            "/api/v1/contact-messages", "/api/v1/newsletter/subscriptions", "/api/v1/newsletter/unsubscribe",
            "/api/v1/coupons/apply", "/api/v1/providers/*/events");

    private static final List<String> PUBLIC_GET = List.of(
            "/api/v1/products/**", "/api/v1/categories/**", "/api/v1/sub-categories/**", "/api/v1/pet-types",
            "/api/v1/stores/**", "/api/v1/providers/**", "/api/v1/service-categories", "/api/v1/services/**",
            "/api/v1/pet-services/provider/*/gallery", "/api/v1/reviews", "/api/v1/reviews/summary",
            "/api/v1/blogs/**", "/api/v1/blog-categories", "/api/v1/hero", "/api/v1/about", "/api/v1/site-services",
            "/api/v1/contact-info", "/api/v1/settings", "/api/v1/files/**");

    private static final List<String> HIDDEN_ALIASES = List.of(
            "/api/v1/cart/clear", "/api/v1/wishlist/clear", "/api/v1/users/profile", "/api/v1/pet-services/provider/profile");

    /** Display order of the tags. */
    private static final Map<String, String> TAGS = new java.util.LinkedHashMap<>();

    static {
        TAGS.put("Auth", "Registration (customer, vendor, service provider), login, tokens, profile and password");
        TAGS.put("Products", "Storefront catalogue: search, filters, facets, best sellers, related products, details");
        TAGS.put("Catalogue", "Public product categories, subcategories and pet types (with product counts)");
        TAGS.put("Stores", "Public store pages and store products");
        TAGS.put("Service Providers", "Public /services pages: categories, provider listing and provider page");
        TAGS.put("Reviews", "Ratings and reviews for products, stores and service providers");
        TAGS.put("Site Content", "Public home-page content, blog, contact form and newsletter");
        TAGS.put("Cart", "Signed-in user's cart with stock checks, discount prices and shipping totals");
        TAGS.put("Wishlist", "Favourite products");
        TAGS.put("Orders", "Checkout, order history, tracking and cancellation for buyers");
        TAGS.put("Coupons", "Coupon validation");
        TAGS.put("Buyer Account", "Buyer dashboard: KPI tiles and notification settings");
        TAGS.put("Buyer Addresses", "Delivery addresses");
        TAGS.put("Files", "Image uploads");
        TAGS.put("Vendor - Dashboard", "Overview KPIs, sales chart and reviews");
        TAGS.put("Vendor - Store & Products", "Own store profile and product management");
        TAGS.put("Vendor - Orders", "Orders received by the vendor's store");
        TAGS.put("Vendor - Finance", "Balance, commission, payouts and bank details");
        TAGS.put("Provider Dashboard", "Service provider's own center");
        TAGS.put("Admin - Operations", "Seller approval, accounts, orders, payouts, stores, coupons, newsletter");
        TAGS.put("Admin - Catalogue", "Categories, subcategories and products of every store");
        TAGS.put("Admin - Content", "Site content, blog, contact messages and review moderation");
    }

    @Bean
    public OpenAPI petShopOpenAPI() {
        String description = """
                **PetShop** is an online marketplace for pet owners. Customers buy pet products from independent stores
                and find grooming, veterinary and training centers. This API is the backend of the whole platform.

                ## What the API covers

                | Area | What it does |
                | --- | --- |
                | **Storefront** | Product catalogue with search, filters, sorting, best sellers, stores, service providers, blog |
                | **Buyer** | Cart, wishlist, checkout, order tracking, addresses, reviews |
                | **Vendor** | Own store, products and stock, received orders, sales dashboard, finance and payouts |
                | **Service provider** | Center profile, price list, team, weekly schedule, gallery, visitor metrics |
                | **Admin** | Seller approval, catalogue and site content, orders, payouts, coupons, review moderation |

                ## How to try it

                1. Open **Auth** and run `POST /api/v1/auth/login` (or register a new account).
                2. The returned token is applied automatically, so locked endpoints work right away.
                   You can also paste it manually with the **Authorize** button.
                3. Open any section, choose an endpoint, press **Try it out**, then **Execute**.

                Endpoints with a lock need a signed-in user; the others are public.
                Use the selector at the top to show one audience only (storefront, vendor, provider, admin).

                ## Roles

                | Role | Can do |
                | --- | --- |
                | `BUYER` | Shop, check out, track orders, write reviews |
                | `VENDOR` | Manage own store, products, orders and payouts |
                | `SERVICE_PROVIDER` | Manage own center, services, team and schedule |
                | `ADMIN` | Approve sellers, manage content, oversee orders and payouts |

                Vendors and service providers start as `PENDING_APPROVAL`. They can sign in and prepare their store,
                but can publish only after an admin approves them.

                ## Main flow

                1. Vendor registers → admin approves → vendor adds products.
                2. Buyer adds products to the cart → checks out (stock is reserved, one order per store).
                3. Vendor moves the order **pending → processing → shipping → delivered**.
                4. Buyer reviews the product → its rating updates.

                ## Response format

                Every response uses the same envelope. Errors also carry a machine-readable `code`.

                ```json
                {
                  "success": true,
                  "message": "Products fetched",
                  "data": { }
                }
                ```

                Lists are paged: `content`, `pageNumber`, `pageSize`, `totalElements`, `totalPages`, `last`
                (`page` starts at 0, `size` is at most 100).

                ## Status codes

                | Code | Meaning |
                | --- | --- |
                | `200` / `201` | Success / created |
                | `400` | Invalid input; field errors are listed in `data` |
                | `401` | Not signed in, token expired or wrong credentials |
                | `403` | Wrong role, not the owner, or account waiting for approval |
                | `404` | Resource not found |
                | `409` | Conflict: duplicate, not enough stock, invalid status change |
                | `429` | Too many requests (login, forms) |

                ## Conventions

                * Money is in AZN (₼) with two decimals; dates are ISO-8601.
                * Images: upload with `POST /api/v1/files`, then send the returned `url`.
                * Inside every section, endpoints follow **GET → POST → PUT → PATCH**, and **DELETE** endpoints are listed last.
                """;
        return new OpenAPI()
                .servers(List.of(new io.swagger.v3.oas.models.servers.Server().url("/").description("Current server")))
                .info(new Info()
                        .title("PetShop API")
                        .version("2.0")
                        .description(description)
                        .contact(new Contact().name("PetShop backend").url("https://pet-shop-frontend-side.vercel.app")))
                .components(new Components().addSecuritySchemes(BEARER, new SecurityScheme()
                        .name(BEARER)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Access token from /api/v1/auth/login")));
    }

    /** Lock icon only on protected operations; ordered tags with descriptions. */
    @Bean
    public OpenApiCustomizer securityAndTagsCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                // Backward-compatible aliases still work but are not shown, so every action appears once.
                HIDDEN_ALIASES.forEach(openApi.getPaths()::remove);
                PathItem reply = openApi.getPaths().get("/api/v1/reviews/{id}/reply");
                if (reply != null) reply.setPut(null);
                openApi.getPaths().forEach((path, item) -> item.readOperationsMap().forEach((method, op) -> {
                    boolean isPublic = PUBLIC_ANY.stream().anyMatch(p -> MATCHER.match(p, path))
                            || (method == PathItem.HttpMethod.GET && PUBLIC_GET.stream().anyMatch(p -> MATCHER.match(p, path)));
                    applySecurity(op, isPublic);
                    addErrorResponses(path, method, op, isPublic);
                }));
            }
            List<Tag> tags = new ArrayList<>();
            TAGS.forEach((name, desc) -> tags.add(new Tag().name(name).description(desc)));
            openApi.setTags(tags);
        };
    }

    /** Documents the error responses each operation can return, with the shared error envelope. */
    private static void addErrorResponses(String path, PathItem.HttpMethod method, Operation op, boolean isPublic) {
        io.swagger.v3.oas.models.responses.ApiResponses responses = op.getResponses();
        if (responses == null) return;
        // The success response keeps the generated schema; give it a readable description.
        responses.forEach((code, r) -> {
            if (code.startsWith("2") && (r.getDescription() == null || r.getDescription().equals("OK")
                    || r.getDescription().equals("Created"))) {
                r.setDescription(code.equals("201") ? "Created" : "Success");
            }
        });
        boolean write = method != PathItem.HttpMethod.GET;
        boolean hasId = path.contains("{");
        if (write || (op.getParameters() != null && !op.getParameters().isEmpty())) {
            error(responses, "400", "Invalid input. Field errors are listed in data.", "VALIDATION_ERROR");
        }
        if (!isPublic) {
            error(responses, "401", "Not signed in or the token has expired.", "UNAUTHORIZED");
            error(responses, "403", "Signed in, but not allowed (wrong role or not the owner).", "FORBIDDEN");
        }
        if (hasId) {
            error(responses, "404", "The resource was not found.", "NOT_FOUND");
        }
        if (write && method != PathItem.HttpMethod.DELETE) {
            error(responses, "409", "Conflict with the current state (duplicate, stock or status rule).", "CONFLICT");
        }
    }

    private static void error(io.swagger.v3.oas.models.responses.ApiResponses responses, String status,
                              String description, String code) {
        if (responses.containsKey(status)) return;
        var example = new java.util.LinkedHashMap<String, Object>();
        example.put("success", false);
        example.put("message", description);
        example.put("data", null);
        example.put("code", code);
        var schema = new io.swagger.v3.oas.models.media.ObjectSchema()
                .addProperty("success", new io.swagger.v3.oas.models.media.BooleanSchema())
                .addProperty("message", new io.swagger.v3.oas.models.media.StringSchema())
                .addProperty("data", new io.swagger.v3.oas.models.media.ObjectSchema().nullable(true))
                .addProperty("code", new io.swagger.v3.oas.models.media.StringSchema());
        responses.addApiResponse(status, new io.swagger.v3.oas.models.responses.ApiResponse()
                .description(description)
                .content(new io.swagger.v3.oas.models.media.Content().addMediaType("application/json",
                        new io.swagger.v3.oas.models.media.MediaType().schema(schema).example(example))));
    }

    private static void applySecurity(Operation op, boolean isPublic) {
        op.setSecurity(isPublic ? List.of() : List.of(new SecurityRequirement().addList(BEARER)));
    }

    @Bean
    public GroupedOpenApi allApi(OpenApiCustomizer securityAndTagsCustomizer) {
        return GroupedOpenApi.builder().group("0-all").displayName("All endpoints")
                .pathsToMatch("/api/**").addOpenApiCustomizer(securityAndTagsCustomizer).build();
    }

    @Bean
    public GroupedOpenApi storefrontApi(OpenApiCustomizer securityAndTagsCustomizer) {
        return GroupedOpenApi.builder().group("1-storefront").displayName("Storefront & buyer")
                .pathsToMatch("/api/v1/auth/**", "/api/v1/users/**", "/api/v1/products/**", "/api/v1/categories/**",
                        "/api/v1/sub-categories/**", "/api/v1/pet-types", "/api/v1/stores/**", "/api/v1/providers/**",
                        "/api/v1/service-categories", "/api/v1/services/**", "/api/v1/reviews/**", "/api/v1/blogs/**",
                        "/api/v1/blog-categories", "/api/v1/hero", "/api/v1/about", "/api/v1/site-services",
                        "/api/v1/contact-info", "/api/v1/settings", "/api/v1/contact-messages", "/api/v1/newsletter/**",
                        "/api/v1/cart/**", "/api/v1/wishlist/**", "/api/v1/orders/**", "/api/v1/coupons/**", "/api/v1/files/**")
                .addOpenApiCustomizer(securityAndTagsCustomizer).build();
    }

    @Bean
    public GroupedOpenApi vendorApi(OpenApiCustomizer securityAndTagsCustomizer) {
        return GroupedOpenApi.builder().group("2-vendor").displayName("Vendor dashboard")
                .pathsToMatch("/api/v1/vendor/**", "/api/v1/reviews/*/reply", "/api/v1/files/**", "/api/v1/auth/**")
                .addOpenApiCustomizer(securityAndTagsCustomizer).build();
    }

    @Bean
    public GroupedOpenApi providerApi(OpenApiCustomizer securityAndTagsCustomizer) {
        return GroupedOpenApi.builder().group("3-provider").displayName("Service provider dashboard")
                .pathsToMatch("/api/v1/pet-services/**", "/api/v1/reviews/*/reply", "/api/v1/files/**", "/api/v1/auth/**")
                .addOpenApiCustomizer(securityAndTagsCustomizer).build();
    }

    @Bean
    public GroupedOpenApi adminApi(OpenApiCustomizer securityAndTagsCustomizer) {
        return GroupedOpenApi.builder().group("4-admin").displayName("Admin")
                .pathsToMatch("/api/v1/admin/**")
                .addOpenApiCustomizer(securityAndTagsCustomizer).build();
    }
}

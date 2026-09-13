package com.petshop.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("PetShop E-Commerce API")
                        .version("1.0")
                        .description("PetShop Platform REST API Documentation"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer sortTagsCustomizer() {
        return openApi -> {
            if (openApi.getTags() != null) {
                List<Tag> tags = new ArrayList<>(openApi.getTags());
                tags.sort((t1, t2) -> {
                    if (t1.getName().toLowerCase().contains("auth")) return -1;
                    if (t2.getName().toLowerCase().contains("auth")) return 1;
                    if (t1.getName().toLowerCase().contains("admin")) return 1;
                    if (t2.getName().toLowerCase().contains("admin")) return -1;
                    return t1.getName().compareTo(t2.getName());
                });
                openApi.setTags(tags);
            }
        };
    }
}
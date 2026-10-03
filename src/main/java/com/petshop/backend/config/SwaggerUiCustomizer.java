package com.petshop.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Customises the built-in Swagger UI page (/swagger-ui/index.html):
 * <ul>
 *   <li>orders operations inside every section resource by resource as GET, POST, PUT, PATCH,
 *       with all DELETE operations at the bottom of the section;</li>
 *   <li>applies the token automatically after a successful login, register or refresh call;</li>
 *   <li>shows inline code in descriptions in red.</li>
 * </ul>
 */
@Configuration
public class SwaggerUiCustomizer {

    /** Extra Swagger UI options injected into the generated swagger-initializer.js. */
    private static final String OPTIONS = """
            operationsSorter: function (a, b) {
                var rank = { get: 0, post: 1, put: 2, patch: 3, delete: 4 };
                var ma = a.get('method'), mb = b.get('method');
                var da = ma === 'delete', db = mb === 'delete';
                if (da !== db) return da ? 1 : -1;
                var resource = function (p) { return p.replace(/\\/\\{[^}]+\\}/g, ''); };
                var ra = resource(a.get('path')), rb = resource(b.get('path'));
                if (ra !== rb) return ra.localeCompare(rb);
                if (rank[ma] !== rank[mb]) return rank[ma] - rank[mb];
                return a.get('path').length - b.get('path').length;
            },
            responseInterceptor: function (res) {
                try {
                    if (res.ok && /\\/api\\/v1\\/auth\\/(login|google|register|refresh-token)/.test(res.url)) {
                        var body = typeof res.body === 'object' ? res.body : JSON.parse(res.text);
                        var token = body && body.data && body.data.token;
                        if (token && window.ui) {
                            window.ui.authActions.authorize({ bearerAuth: {
                                name: 'bearerAuth', schema: { type: 'http', scheme: 'bearer' }, value: token } });
                        }
                    }
                } catch (e) { /* not JSON */ }
                return res;
            },
            """;

    /** Shows inline code in descriptions (endpoints, field names) in red instead of Swagger's default purple. */
    private static final String STYLE = """
            (function () {
                var style = document.createElement('style');
                style.textContent = '.swagger-ui .markdown code, .swagger-ui .renderedMarkdown code { color: #d32f2f !important; }';
                document.head.appendChild(style);
            })();
            """;

    @Bean
    public SwaggerIndexTransformer petshopIndexPageTransformer(SwaggerUiConfigProperties config,
                                                       SwaggerUiOAuthProperties oauth,
                                                       SwaggerUiConfigParameters parameters,
                                                       SwaggerWelcomeCommon welcome,
                                                       ObjectMapperProvider objectMapperProvider) {
        return new SwaggerIndexPageTransformer(config, oauth, parameters, welcome, objectMapperProvider) {
            @Override
            public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain chain)
                    throws IOException {
                Resource transformed = super.transform(request, resource, chain);
                String name = resource.getFilename();
                if ("swagger-initializer.js".equals(name)) {
                    String js = new String(transformed.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    js = STYLE + js.replace("SwaggerUIBundle({", "SwaggerUIBundle({\n" + OPTIONS);
                    return new TransformedResource(resource, js.getBytes(StandardCharsets.UTF_8));
                }
                return transformed;
            }
        };
    }
}

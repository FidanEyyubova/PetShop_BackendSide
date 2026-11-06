package com.petshop.backend.security;

import com.petshop.backend.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = 60_000;

    private final AppProperties.RateLimit config;
    private final JsonErrorWriter errorWriter;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitFilter(AppProperties properties, JsonErrorWriter errorWriter) {
        this.config = properties.getRateLimit();
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        if (!config.isEnabled() || !"POST".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        String bucket = null;
        int limit = 0;
        if (path.startsWith("/api/v1/auth/login") || path.startsWith("/api/v1/auth/google") || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/forgot-password") || path.startsWith("/api/v1/auth/reset-password")
                || path.startsWith("/api/v1/auth/refresh-token")) {
            bucket = "auth";
            limit = config.getAuthPerMinute();
        } else if (path.startsWith("/api/v1/contact-messages") || path.startsWith("/api/v1/newsletter")) {
            bucket = "forms";
            limit = config.getFormsPerMinute();
        } else if (path.equals("/api/v1/reviews")) {
            bucket = "reviews";
            limit = config.getReviewsPerMinute();
        } else if (path.startsWith("/api/v1/providers/") && path.endsWith("/events")) {
            bucket = "events";
            limit = config.getEventsPerMinute();
        }

        if (bucket != null && !tryAcquire(bucket + ":" + clientIp(request), limit)) {
            response.setHeader("Retry-After", "60");
            errorWriter.write(response, 429, "RATE_LIMITED", "Too many requests. Please try again in a minute.");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean tryAcquire(String key, int limit) {
        long now = System.currentTimeMillis();
        if (windows.size() > 50_000) {
            windows.entrySet().removeIf(e -> now - e.getValue().start > WINDOW_MILLIS);
        }
        Window w = windows.compute(key, (k, existing) ->
                existing == null || now - existing.start > WINDOW_MILLIS ? new Window(now) : existing);
        synchronized (w) {
            return ++w.count <= limit;
        }
    }

    private String clientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private static final class Window {
        final long start;
        int count;

        Window(long start) {
            this.start = start;
        }
    }
}

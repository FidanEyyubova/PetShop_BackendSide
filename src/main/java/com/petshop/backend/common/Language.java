package com.petshop.backend.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Locale;

public enum Language {
    AZ, EN, RU;

    public static final Language DEFAULT = AZ;
    public static final List<Language> SECONDARY = List.of(EN, RU);
    public static final String QUERY_PARAM = "lang";

    public String code() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Locale locale() {
        return Locale.forLanguageTag(code());
    }

    public static Language fromCode(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim().toLowerCase(Locale.ROOT);
        if (v.length() > 2) v = v.substring(0, 2);
        return switch (v) {
            case "az" -> AZ;
            case "en" -> EN;
            case "ru" -> RU;
            default -> null;
        };
    }

    public static Language current() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servlet)) return DEFAULT;
        return of(servlet.getRequest());
    }

    public static Language of(HttpServletRequest request) {
        Language fromParam = fromCode(request.getParameter(QUERY_PARAM));
        if (fromParam != null) return fromParam;
        String header = request.getHeader("Accept-Language");
        if (header != null) {
            for (String part : header.split(",")) {
                Language lang = fromCode(part.split(";")[0]);
                if (lang != null) return lang;
            }
        }
        return DEFAULT;
    }
}

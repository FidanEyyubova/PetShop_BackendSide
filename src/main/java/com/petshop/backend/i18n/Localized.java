package com.petshop.backend.i18n;

import com.petshop.backend.common.Language;
import com.petshop.backend.util.JsonUtils;

import java.util.*;

public final class Localized {

    private final Translations translations;
    private final Language language;

    private Localized(Translations translations, Language language) {
        this.translations = translations == null ? Translations.empty() : translations;
        this.language = language;
    }

    public static Localized of(Translations translations) {
        return new Localized(translations, Language.current());
    }

    public static Localized of(Translations translations, Language language) {
        return new Localized(translations, language);
    }

    public String text(String field, String base) {
        if (language == Language.DEFAULT) return base;
        Object value = translations.get(language, field);
        return value instanceof String s && !s.isBlank() ? s : base;
    }

    public List<String> list(String field, List<String> base) {
        if (language == Language.DEFAULT) return base;
        Object value = translations.get(language, field);
        if (value instanceof Collection<?> items && !items.isEmpty()) {
            return items.stream().map(String::valueOf).toList();
        }
        return base;
    }

    public <T> T object(String field, T base, Class<T> type) {
        if (language == Language.DEFAULT) return base;
        Object value = translations.get(language, field);
        if (value == null) return base;
        try {
            if (value instanceof Map<?, ?> translated && base != null) {
                Map<String, Object> merged = JsonUtils.MAPPER.convertValue(base, new com.fasterxml.jackson.core.type.TypeReference<LinkedHashMap<String, Object>>() {});
                translated.forEach((k, v) -> {
                    if (v == null || (v instanceof String s && s.isBlank()) || (v instanceof Collection<?> c && c.isEmpty())) return;
                    merged.put(String.valueOf(k), v);
                });
                value = merged;
            }
            T converted = JsonUtils.MAPPER.convertValue(value, type);
            return converted != null ? converted : base;
        } catch (IllegalArgumentException e) {
            return base;
        }
    }

    public static Map<String, Map<String, Object>> editable(Translations translations, Object... azFields) {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        Map<String, Object> az = new LinkedHashMap<>();
        for (int i = 0; i + 1 < azFields.length; i += 2) {
            Object value = azFields[i + 1];
            if (value == null) continue;
            if (value instanceof String s && s.isBlank()) continue;
            if (value instanceof Collection<?> c && c.isEmpty()) continue;
            az.put((String) azFields[i], value);
        }
        result.put(Language.AZ.code(), az);
        Translations stored = translations == null ? Translations.empty() : translations;
        for (Language lang : Language.SECONDARY) {
            Map<String, Object> fields = stored.asMap().get(lang.code());
            result.put(lang.code(), fields == null ? new LinkedHashMap<>() : new LinkedHashMap<>(fields));
        }
        return result;
    }
}

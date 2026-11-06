package com.petshop.backend.i18n;

import com.petshop.backend.common.Language;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.util.JsonUtils;

import java.util.*;

public final class Translations {

    public static final int MAX_TEXT_LENGTH = 20_000;
    public static final int MAX_LIST_SIZE = 50;

    private final Map<String, Map<String, Object>> values;

    private Translations(Map<String, Map<String, Object>> values) {
        this.values = values;
    }

    public static Translations empty() {
        return new Translations(new LinkedHashMap<>());
    }

    public static Translations of(Map<String, Map<String, Object>> raw) {
        return raw == null ? empty() : new Translations(new LinkedHashMap<>(raw));
    }

    public static Translations fromRequest(Map<String, Map<String, Object>> raw, Set<String> allowedFields) {
        Translations result = empty();
        if (raw == null) return result;
        for (Language lang : Language.SECONDARY) {
            Map<String, Object> fields = raw.get(lang.code());
            if (fields == null) continue;
            Map<String, Object> clean = new LinkedHashMap<>();
            fields.forEach((field, value) -> {
                if (!allowedFields.contains(field)) return;
                Object normalized = normalize(field, value);
                if (normalized != null) clean.put(field, normalized);
            });
            if (!clean.isEmpty()) result.values.put(lang.code(), clean);
        }
        return result;
    }

    public Translations mergedWith(Map<String, Map<String, Object>> raw, Set<String> allowedFields) {
        if (raw == null) return this;
        Translations incoming = fromRequest(raw, allowedFields);
        Map<String, Map<String, Object>> merged = new LinkedHashMap<>(values);
        for (Language lang : Language.SECONDARY) {
            if (!raw.containsKey(lang.code())) continue;
            Map<String, Object> fields = new LinkedHashMap<>(merged.getOrDefault(lang.code(), Map.of()));
            fields.keySet().removeAll(allowedFields);
            Map<String, Object> updates = incoming.values.get(lang.code());
            if (updates != null) fields.putAll(updates);
            if (fields.isEmpty()) merged.remove(lang.code());
            else merged.put(lang.code(), fields);
        }
        return new Translations(merged);
    }

    public Object get(Language lang, String field) {
        Map<String, Object> fields = values.get(lang.code());
        return fields == null ? null : fields.get(field);
    }

    public Map<String, Map<String, Object>> asMap() {
        return values;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public String toJson() {
        return values.isEmpty() ? null : JsonUtils.write(values);
    }

    @SuppressWarnings("unchecked")
    public static Translations fromJson(String json) {
        if (json == null || json.isBlank()) return empty();
        Map<String, Map<String, Object>> parsed = JsonUtils.read(json, LinkedHashMap.class);
        return parsed == null ? empty() : new Translations(parsed);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Translations other && values.equals(other.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    private static Object normalize(String field, Object value) {
        if (value == null) return null;
        if (value instanceof String s) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) return null;
            if (trimmed.length() > MAX_TEXT_LENGTH) {
                throw new BadRequestException("TRANSLATION_TOO_LONG", "Translation of " + field + " is too long");
            }
            return trimmed;
        }
        if (value instanceof Collection<?> list) {
            if (list.size() > MAX_LIST_SIZE) {
                throw new BadRequestException("TRANSLATION_TOO_LONG", "Translation of " + field + " is too long");
            }
            List<Object> clean = new ArrayList<>();
            for (Object item : list) {
                Object normalized = normalize(field, item);
                if (normalized != null) clean.add(normalized);
            }
            return clean.isEmpty() ? null : clean;
        }
        if (value instanceof Map<?, ?> map) {
            if (JsonUtils.write(map).length() > MAX_TEXT_LENGTH) {
                throw new BadRequestException("TRANSLATION_TOO_LONG", "Translation of " + field + " is too long");
            }
            return map.isEmpty() ? null : map;
        }
        return value;
    }
}

package com.petshop.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JSON helpers for TEXT columns that store lists or small objects.
 * Reading is tolerant: legacy comma-separated values are split into a list.
 */
public final class JsonUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private JsonUtils() {}

    public static String write(Object value) {
        if (value == null) return null;
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize value", e);
        }
    }

    public static List<String> readStringList(String raw) {
        if (raw == null || raw.isBlank()) return new ArrayList<>();
        String trimmed = raw.trim();
        if (trimmed.startsWith("[")) {
            try {
                return new ArrayList<>(MAPPER.readValue(trimmed, new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException ignored) {
                // fall through to comma-separated parsing
            }
        }
        return new ArrayList<>(Arrays.stream(trimmed.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList());
    }

    public static <T> T read(String raw, Class<T> type) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return MAPPER.readValue(raw, type);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}

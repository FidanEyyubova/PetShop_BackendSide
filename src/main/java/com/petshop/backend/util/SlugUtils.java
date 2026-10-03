package com.petshop.backend.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Predicate;

public final class SlugUtils {

    private SlugUtils() {}

    /** Lower-case ASCII slug; Azerbaijani letters are transliterated (ə→e, ı→i, ş→s ...). */
    public static String slugify(String input) {
        if (input == null || input.isBlank()) return "item";
        String s = input.trim().toLowerCase(Locale.ROOT)
                .replace("ə", "e").replace("ı", "i").replace("ş", "s").replace("ç", "c")
                .replace("ğ", "g").replace("ö", "o").replace("ü", "u");
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
        if (s.length() > 120) s = s.substring(0, 120).replaceAll("-+$", "");
        return s.isBlank() ? "item" : s;
    }

    /** Returns base slug, or base-2, base-3 ... until {@code taken} reports it is free. */
    public static String unique(String input, Predicate<String> taken) {
        String base = slugify(input);
        String slug = base;
        int n = 2;
        while (taken.test(slug)) {
            slug = base + "-" + n++;
        }
        return slug;
    }
}

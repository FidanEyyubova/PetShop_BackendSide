package com.petshop.backend.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtils {

    public static final int MAX_PAGE_SIZE = 100;

    private PageUtils() {}

    /** Page request with page >= 0 and 1 <= size <= 100. */
    public static Pageable of(int page, int size, Sort sort) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, sort == null ? Sort.unsorted() : sort);
    }

    public static Pageable of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    /** Sort on a whitelisted property; anything else falls back to {@code defaultProperty}. */
    public static Sort sort(String property, String direction, String defaultProperty, String... allowed) {
        String prop = defaultProperty;
        if (property != null) {
            for (String a : allowed) {
                if (a.equalsIgnoreCase(property)) {
                    prop = a;
                    break;
                }
            }
        }
        Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(dir, prop);
    }
}

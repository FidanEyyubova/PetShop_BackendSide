package com.petshop.backend.i18n;

import java.util.Set;

public final class TranslatableFields {

    public static final Set<String> PRODUCT = Set.of("title", "description", "badges", "flavors",
            "keyBenefits", "guarantees", "ingredients", "nutritionInfo", "usageGuide");
    public static final Set<String> CATEGORY = Set.of("title");
    public static final Set<String> SUB_CATEGORY = Set.of("title");
    public static final Set<String> STORE = Set.of("tagline", "description", "deliveryInfo", "features");
    public static final Set<String> STORE_BADGE = Set.of("badgeText");
    public static final Set<String> BLOG = Set.of("title", "excerpt", "content", "tags", "readTime");
    public static final Set<String> BLOG_CATEGORY = Set.of("title");
    public static final Set<String> HERO = Set.of("title", "subtitle");
    public static final Set<String> ABOUT = Set.of("title", "subtitle", "description");
    public static final Set<String> ADMIN_SERVICE = Set.of("title", "subtitle");
    public static final Set<String> CONTACT_INFO = Set.of("location", "workingHours");
    public static final Set<String> SERVICE = Set.of("title", "description", "durationText");
    public static final Set<String> PROVIDER = Set.of("bio", "tags");
    public static final Set<String> TEAM_MEMBER = Set.of("role", "experience", "certifications", "bio", "note");
    public static final Set<String> GALLERY = Set.of("title");

    private TranslatableFields() {}
}

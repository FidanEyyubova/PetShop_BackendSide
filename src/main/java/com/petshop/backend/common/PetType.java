package com.petshop.backend.common;

import java.util.Locale;

/**
 * Pet types used by the storefront filters. The id is the slug the frontend uses (dogs, small-pets ...).
 */
public enum PetType {
    DOGS("dogs", "Dogs", "İtlər", "dog"),
    CATS("cats", "Cats", "Pişiklər", "cat"),
    BIRDS("birds", "Birds", "Quşlar", "bird"),
    FISH("fish", "Fish", "Balıqlar", "fish"),
    SMALL_PETS("small-pets", "Small Pets", "Kiçik heyvanlar", "rabbit"),
    REPTILES("reptiles", "Reptiles", "Sürünənlər", "turtle");

    private final String id;
    private final String displayName;
    private final String azName;
    private final String iconName;

    PetType(String id, String displayName, String azName, String iconName) {
        this.id = id;
        this.displayName = displayName;
        this.azName = azName;
        this.iconName = iconName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getAzName() { return azName; }
    public String getIconName() { return iconName; }

    /** Maps "dog", "Dogs", "rodent", "small_pets" ... to the enum; null for blank input. */
    public static PetType parse(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim().toLowerCase(Locale.ROOT).replace('_', '-').replace(' ', '-');
        return switch (v) {
            case "dog", "dogs" -> DOGS;
            case "cat", "cats" -> CATS;
            case "bird", "birds" -> BIRDS;
            case "fish", "fishes" -> FISH;
            case "small-pet", "small-pets", "smallpets", "rodent", "rodents", "rabbit", "hamster" -> SMALL_PETS;
            case "reptile", "reptiles" -> REPTILES;
            default -> throw new IllegalArgumentException("Unknown pet type: " + value
                    + ". Allowed: dogs, cats, birds, fish, small-pets, reptiles");
        };
    }

    /** Canonical id for a raw value, or null when blank. */
    public static String normalizeId(String value) {
        PetType type = parse(value);
        return type == null ? null : type.id;
    }
}

package com.petshop.backend.common;

import java.util.Locale;

public enum PetType {
    DOGS("dogs", "Dogs", "İtlər", "Собаки", "dog"),
    CATS("cats", "Cats", "Pişiklər", "Кошки", "cat"),
    BIRDS("birds", "Birds", "Quşlar", "Птицы", "bird"),
    FISH("fish", "Fish", "Balıqlar", "Рыбы", "fish"),
    SMALL_PETS("small-pets", "Small Pets", "Kiçik heyvanlar", "Мелкие животные", "rabbit"),
    REPTILES("reptiles", "Reptiles", "Sürünənlər", "Рептилии", "turtle");

    private final String id;
    private final String displayName;
    private final String azName;
    private final String ruName;
    private final String iconName;

    PetType(String id, String displayName, String azName, String ruName, String iconName) {
        this.id = id;
        this.displayName = displayName;
        this.azName = azName;
        this.ruName = ruName;
        this.iconName = iconName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getAzName() { return azName; }
    public String getRuName() { return ruName; }
    public String label(Language language) {
        return switch (language) {
            case AZ -> azName;
            case RU -> ruName;
            case EN -> displayName;
        };
    }
    public String getIconName() { return iconName; }

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

    public static String normalizeId(String value) {
        PetType type = parse(value);
        return type == null ? null : type.id;
    }
}

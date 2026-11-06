package com.petshop.backend.common;

public enum ServiceType {
    GROOMING("Grooming", "Qulluq", "Груминг"),
    VETERINARY("Veterinary", "Baytarlıq", "Ветеринария"),
    TRAINING("Training", "Təlim", "Дрессировка");

    private final String label;
    private final String azLabel;
    private final String ruLabel;

    ServiceType(String label, String azLabel, String ruLabel) {
        this.label = label;
        this.azLabel = azLabel;
        this.ruLabel = ruLabel;
    }

    public String getLabel() { return label; }
    public String getAzLabel() { return azLabel; }
    public String getRuLabel() { return ruLabel; }
    public String label(Language language) {
        return switch (language) {
            case AZ -> azLabel;
            case RU -> ruLabel;
            case EN -> label;
        };
    }
    public String getSlug() { return name().toLowerCase(); }

    public static ServiceType parse(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim().toUpperCase();
        if (v.equals("VET")) return VETERINARY;
        try {
            return ServiceType.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown service type: " + value
                    + ". Allowed: grooming, veterinary, training");
        }
    }
}

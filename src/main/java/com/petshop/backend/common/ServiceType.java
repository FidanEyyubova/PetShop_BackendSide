package com.petshop.backend.common;

/** Service categories shown on /services. */
public enum ServiceType {
    GROOMING("Grooming", "Qulluq"),
    VETERINARY("Veterinary", "Baytarlıq"),
    TRAINING("Training", "Təlim");

    private final String label;
    private final String azLabel;

    ServiceType(String label, String azLabel) {
        this.label = label;
        this.azLabel = azLabel;
    }

    public String getLabel() { return label; }
    public String getAzLabel() { return azLabel; }
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

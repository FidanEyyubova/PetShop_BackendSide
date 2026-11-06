package com.petshop.backend.common;

public final class ValidationPatterns {

    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$";
    public static final String PASSWORD_MESSAGE =
            "Password must be 8-72 characters and contain at least one letter and one digit";

    public static final String PHONE = "^\\+?[0-9][0-9 ()-]{6,19}$";
    public static final String PHONE_MESSAGE = "Phone must look like +994 50 123 45 67";

    public static final String VOEN = "^\\d{10}$";
    public static final String VOEN_MESSAGE = "VÖEN must be exactly 10 digits";

    public static final String IBAN = "^AZ\\d{2}\\s?[A-Z]{4}(\\s?[A-Z0-9]){20}$";
    public static final String IBAN_MESSAGE = "IBAN must be a valid Azerbaijani IBAN (AZ.. 28 characters)";

    public static final String IMAGE_URL = "^(https?://[^\\s]{3,}|/api/v1/files/[A-Za-z0-9._-]+)$";
    public static final String IMAGE_URL_MESSAGE = "Must be an http(s) URL or an uploaded file URL (/api/v1/files/...)";

    public static final String URL = "^https?://[^\\s]{3,}$";
    public static final String URL_MESSAGE = "Must be an http(s) URL";

    public static final String TIME = "^([01]\\d|2[0-3]):[0-5]\\d$";

    private ValidationPatterns() {}
}

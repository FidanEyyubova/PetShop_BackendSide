package com.petshop.backend.i18n;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TranslationsConverter implements AttributeConverter<Translations, String> {

    @Override
    public String convertToDatabaseColumn(Translations attribute) {
        return attribute == null ? null : attribute.toJson();
    }

    @Override
    public Translations convertToEntityAttribute(String dbData) {
        return Translations.fromJson(dbData);
    }
}

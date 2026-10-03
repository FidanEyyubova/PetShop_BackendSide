package com.petshop.backend.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

/** Stores a List&lt;String&gt; as a JSON array in a TEXT column; reads legacy comma-separated text too. */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        return JsonUtils.write(attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return dbData == null ? new ArrayList<>() : JsonUtils.readStringList(dbData);
    }
}

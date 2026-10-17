package com.bodeguita.bodeguita_backend.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return attribute == null ? null : (attribute ? "1" : "0");
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        String limpio = dbData.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        return "1".equals(limpio) || "S".equalsIgnoreCase(limpio) || "T".equalsIgnoreCase(limpio);
    }
}

package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.PlaceCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class PlaceCategoryDatabaseConverter implements AttributeConverter<PlaceCategory, String> {

    @Override
    public String convertToDatabaseColumn(PlaceCategory category) {
        if (category == null) {
            return null;
        }

        return category.name();
    }

    @Override
    public PlaceCategory convertToEntityAttribute(String columnValue) {
        if (columnValue == null) {
            return null;
        }

        return Stream.of(PlaceCategory.values())
            .filter(c -> c.name().equals(columnValue))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

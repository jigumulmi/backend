package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.PlaceCategoryGroup;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class PlaceCategoryGroupDatabaseConverter implements AttributeConverter<PlaceCategoryGroup, String> {

    @Override
    public String convertToDatabaseColumn(PlaceCategoryGroup categoryGroup) {
        if (categoryGroup == null) {
            return null;
        }

        return categoryGroup.name();
    }

    @Override
    public PlaceCategoryGroup convertToEntityAttribute(String columnValue) {
        if (columnValue == null) {
            return null;
        }

        return Stream.of(PlaceCategoryGroup.values())
            .filter(c -> c.name().equals(columnValue))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

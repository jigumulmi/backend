package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.District;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class DistrictDatabaseConverter implements AttributeConverter<District, String> {

    @Override
    public String convertToDatabaseColumn(District district) {
        if (district == null) {
            return null;
        }

        return district.getTitle();
    }

    @Override
    public District convertToEntityAttribute(String columnValue) {
        if (columnValue == null) {
            return null;
        }

        return Stream.of(District.values())
            .filter(district -> district.getTitle().equals(columnValue))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

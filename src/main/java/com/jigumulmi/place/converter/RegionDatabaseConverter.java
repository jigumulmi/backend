package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.Region;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RegionDatabaseConverter implements AttributeConverter<Region, String> {

    @Override
    public String convertToDatabaseColumn(Region region) {
        if (region == null) {
            return null;
        }

        return region.getTitle();
    }

    @Override
    public Region convertToEntityAttribute(String columnValue) {
        if (columnValue == null) {
            return null;
        }

        return Stream.of(Region.values())
            .filter(region -> region.getTitle().equals(columnValue))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

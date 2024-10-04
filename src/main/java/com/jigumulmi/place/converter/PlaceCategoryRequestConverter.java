package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.PlaceCategory;
import org.springframework.core.convert.converter.Converter;

public class PlaceCategoryRequestConverter implements Converter<String, PlaceCategory> {

    @Override
    public PlaceCategory convert(String title) {
        return PlaceCategory.ofTitle(title);
    }
}

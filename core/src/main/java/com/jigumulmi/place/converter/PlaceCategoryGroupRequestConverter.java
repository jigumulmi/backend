package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.PlaceCategoryGroup;
import org.springframework.core.convert.converter.Converter;

public class PlaceCategoryGroupRequestConverter implements Converter<String, PlaceCategoryGroup> {

    @Override
    public PlaceCategoryGroup convert(String title) {
        return PlaceCategoryGroup.ofTitle(title);
    }
}

package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.Region;
import org.springframework.core.convert.converter.Converter;

public class RegionRequestConverter implements Converter<String, Region> {

    @Override
    public Region convert(String title) {
        return Region.ofTitle(title);
    }
}

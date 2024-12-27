package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.District;
import org.springframework.core.convert.converter.Converter;

public class DistrictRequestConverter implements Converter<String, District> {

    @Override
    public District convert(String id) {
        return District.ofId(Integer.valueOf(id));
    }
}

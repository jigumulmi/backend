package com.jigumulmi.place.converter;

import com.jigumulmi.place.vo.District;
import org.springframework.core.convert.converter.Converter;

public class DistrictRequestConverter implements Converter<Integer, District> {

    @Override
    public District convert(Integer id) {
        return District.ofId(id);
    }
}

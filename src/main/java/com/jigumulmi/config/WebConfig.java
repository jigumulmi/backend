package com.jigumulmi.config;

import com.jigumulmi.place.converter.PlaceCategoryGroupRequestConverter;
import com.jigumulmi.place.converter.PlaceCategoryRequestConverter;
import com.jigumulmi.place.domain.PlaceCategoryMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PlaceCategoryGroupRequestConverter());
        registry.addConverter(new PlaceCategoryRequestConverter());
    }
}

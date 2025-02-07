package com.jigumulmi.config;

import com.jigumulmi.config.logging.LoggingInterceptor;
import com.jigumulmi.place.converter.DistrictRequestConverter;
import com.jigumulmi.place.converter.PlaceCategoryGroupRequestConverter;
import com.jigumulmi.place.converter.PlaceCategoryRequestConverter;
import com.jigumulmi.place.converter.RegionRequestConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PlaceCategoryGroupRequestConverter());
        registry.addConverter(new PlaceCategoryRequestConverter());
        registry.addConverter(new RegionRequestConverter());
        registry.addConverter(new DistrictRequestConverter());
    }
}

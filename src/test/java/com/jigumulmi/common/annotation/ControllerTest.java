package com.jigumulmi.common.annotation;

import com.jigumulmi.config.security.SecurityConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest
@Import(SecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
public @interface ControllerTest {
    @AliasFor(annotation = WebMvcTest.class, attribute = "value")
    Class<?>[] value() default {};
}

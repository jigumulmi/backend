package com.jigumulmi.place.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BusinessHourMultiFieldValidator.class)
public @interface ValidBusinessHour {
    String message() default "Invalid business hour configuration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

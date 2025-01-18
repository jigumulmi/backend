package com.jigumulmi.admin.place.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BusinessHourMultiFieldValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBusinessHour {
    String message() default "Invalid business hour configuration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

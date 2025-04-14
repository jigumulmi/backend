package com.jigumulmi.place.dto.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [BusinessHourMultiFieldValidator::class])
annotation class ValidBusinessHour(
    val message: String = "Invalid business hour configuration",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
package com.onoff.heatmap.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HourValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHour {

    String message() default "Invalid hour";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.onoff.heatmap.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NumberOfShadesValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNumberOfShades {
    String message() default "Invalid numberOfShades";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
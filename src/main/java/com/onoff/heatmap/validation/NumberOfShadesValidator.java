package com.onoff.heatmap.validation;

import com.onoff.heatmap.config.RequestConstraintsProps;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberOfShadesValidator implements ConstraintValidator<ValidNumberOfShades, Integer> {

    private final RequestConstraintsProps constraints;

    public NumberOfShadesValidator(RequestConstraintsProps constraints) {
        this.constraints = constraints;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return true; // return to @NotNull validation
        int min = constraints.getNumberOfShadesMin();
        int max = constraints.getNumberOfShadesMax();

        boolean valid = value >= min && value <= max;

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("must be between %d and %d", min, max)
            ).addConstraintViolation();
        }

        return valid;
    }
}
package com.onoff.heatmap.validation;

import com.onoff.heatmap.config.RequestConstraintsProps;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HourValidator implements ConstraintValidator<ValidHour, Integer> {

    private final RequestConstraintsProps properties;

    public HourValidator(RequestConstraintsProps properties) {
        this.properties = properties;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return true;
        int min = properties.getHourMin();
        int max = properties.getHourMax();

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

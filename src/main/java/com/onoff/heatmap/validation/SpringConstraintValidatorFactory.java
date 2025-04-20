package com.onoff.heatmap.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory {

    private final ApplicationContext applicationContext;

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return applicationContext.getAutowireCapableBeanFactory().createBean(key);
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {}
}

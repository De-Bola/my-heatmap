package com.onoff.heatmap.config;

import com.onoff.heatmap.validation.SpringConstraintValidatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfig {

    private final SpringConstraintValidatorFactory validatorFactory;

    public ValidationConfig(SpringConstraintValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setConstraintValidatorFactory(validatorFactory);
        return factoryBean;
    }
}

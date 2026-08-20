package com.burkina.marketplace.validation.annotation;

import com.burkina.marketplace.validation.validator.UniqueInnValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ ElementType.FIELD })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UniqueInnValidator.class)
public @interface UniqueInn {

    String message() default "Inn already taken";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

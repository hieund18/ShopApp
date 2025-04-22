package com.project.shopapp.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ShippingDateValidator implements ConstraintValidator<ShippingDateConstraint, LocalDate> {
    private int min;

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(localDate)) return true;

        long date = ChronoUnit.DAYS.between(LocalDate.now(), localDate);

        return date >= min;
    }

    @Override
    public void initialize(ShippingDateConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

        min = constraintAnnotation.min();
    }
}

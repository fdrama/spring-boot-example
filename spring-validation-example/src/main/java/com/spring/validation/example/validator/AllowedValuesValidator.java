package com.spring.validation.example.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author fdrama
 * date 2023年05月17日 11:45
 */
public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, Object> {
    private String[] allowedValues;
    private int[] allowedIntValues;

    @Override
    public void initialize(AllowedValues annotation) {
        this.allowedValues = annotation.value();
        this.allowedIntValues = annotation.intValue();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            String strValue = (String) value;
            for (String allowedValue : allowedValues) {
                if (allowedValue.equals(strValue)) {
                    return true;
                }
            }
        } else if (value instanceof Integer) {
            int intValue = (int) value;
            for (int allowedIntValue : allowedIntValues) {
                if (allowedIntValue == intValue) {
                    return true;
                }
            }
        }
        return false;
    }
}
package com.spring.validation.example.validator;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

/**
 * @author fdrama
 * date 2023年05月17日 16:49
 */
public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {
    private String startDateFieldName;
    private String endDateFieldName;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        startDateFieldName = constraintAnnotation.startDate();
        endDateFieldName = constraintAnnotation.endDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
            Date startDate = (Date) beanWrapper.getPropertyValue(startDateFieldName);
            Date endDate = (Date) beanWrapper.getPropertyValue(endDateFieldName);
            if (startDate != null && endDate != null) {
                return !startDate.after(endDate);
            }
        } catch (Exception e) {
            // ignore
        }
        return true;
    }
}
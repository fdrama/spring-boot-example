package com.spring.validation.example.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fdrama
 * date 2023年05月17日 16:49
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DateRangeValidator.class})
public @interface DateRange {
    String message() default "开始日期不能大于结束日期";

    String startDate() default "startDate";

    String endDate() default "endDate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
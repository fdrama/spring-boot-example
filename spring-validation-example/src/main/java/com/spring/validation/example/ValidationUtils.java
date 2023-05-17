package com.spring.validation.example;

import org.hibernate.validator.HibernateValidator;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author fdrama
 * @date 2022/05/10
 **/
public class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * 验证器
     */
    public static final Validator VALIDATOR_ALL = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(false)
            .buildValidatorFactory()
            .getValidator();

    public static final Validator VALIDATOR_FAST = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory()
            .getValidator();

    /**
     * 快速失败校验
     *
     * @param obj obj
     * @param <T> t
     * @return validationResult
     */
    public static <T> ValidationResult validateFast(T obj) {
        Set<ConstraintViolation<T>> validateSet = VALIDATOR_FAST.validate(obj, Default.class);
        return buildValidationResult(validateSet);
    }

    /**
     * 快速失败分组校验
     *
     * @param obj obj
     * @param <T> t
     * @return validationResult
     */
    public static <T> ValidationResult validateFast(T obj, Class<?>... groups) {
        if (groups != null && groups.length > 0) {
            Set<ConstraintViolation<T>> validateSet = VALIDATOR_FAST.validate(obj, groups);
            return buildValidationResult(validateSet);
        } else {
            return validateFast(obj);
        }
    }

    public static <T> ValidationResult validate(T obj) {
        Set<ConstraintViolation<T>> validateSet = VALIDATOR_ALL.validate(obj, Default.class);
        return buildValidationResult(validateSet);
    }

    /**
     * 分组校验
     *
     * @param obj    obj
     * @param groups groups
     * @param <T>    T
     * @return ValidationResult
     */
    public static <T> ValidationResult validate(T obj, Class<?>... groups) {
        if (groups != null && groups.length > 0) {
            Set<ConstraintViolation<T>> validateSet = VALIDATOR_ALL.validate(obj, groups);
            return buildValidationResult(validateSet);
        } else {
            return validate(obj);
        }
    }


    /**
     * @param clazz      校验类
     * @param fieldName  属性名称
     * @param fieldValue 属性值
     * @param <T>        T
     * @return ValidationResult
     */
    public static <T> ValidationResult validationValue(Class<T> clazz, String fieldName, Object fieldValue) {
        Set<ConstraintViolation<T>> validateSet = VALIDATOR_ALL.validateValue(clazz, fieldName, fieldValue);
        return buildValidationResult(validateSet);
    }

    /**
     * @param obj       实体类
     * @param fieldName 校验属性
     * @param <T>       T
     * @return ValidationResult
     */
    public static <T> ValidationResult validationProperty(T obj, String fieldName) {
        Set<ConstraintViolation<T>> validateSet = VALIDATOR_ALL.validateProperty(obj, fieldName);
        return buildValidationResult(validateSet);
    }


    public static <T> ValidationResult buildValidationResult(Set<ConstraintViolation<T>> validateSet) {
        ValidationResult validationResult = new ValidationResult();

        if (!CollectionUtils.isEmpty(validateSet)) {
            Map<String, String> errorMsgMap = new HashMap<>(validateSet.size());
            for (ConstraintViolation<T> constraintViolation : validateSet) {
                errorMsgMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
            }
            validationResult.setHasErrors(true);
            validationResult.setErrorMsg(errorMsgMap);
        }
        return validationResult;
    }

}

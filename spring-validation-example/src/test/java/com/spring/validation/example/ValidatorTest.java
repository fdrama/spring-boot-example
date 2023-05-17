package com.spring.validation.example;

import com.spring.validation.example.request.ContractRequest;
import com.spring.validation.example.request.OrderRequest;
import com.spring.validation.example.request.ProductRequest;
import com.spring.validation.example.request.StudentRequest;
import com.spring.validation.example.request.TimeRangeRequest;
import com.spring.validation.example.request.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

/**
 * @author fdrama
 * date 2023年05月17日 11:27
 */
@SpringBootTest
@Slf4j
public class ValidatorTest {


    @Test
    void test() {
        ValidationResult validate = ValidationUtils.validate(new StudentRequest());
        log.info(validate.getMessage());
    }

    @Test
    void testChild() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductList(Collections.singletonList(new ProductRequest("1", new BigDecimal("0"))));
        ValidationResult validate = ValidationUtils.validate(orderRequest);
        log.info(validate.getMessage());
    }


    @Test
    void testCustomAnnotation() {
        StudentRequest studentRequest = new StudentRequest();
        studentRequest.setGender(2);
        ValidationResult validate = ValidationUtils.validate(studentRequest);
        log.info(validate.getMessage());
    }

    @Test
    void tesGroupValidate() {

    }

    @Test
    void testGroupSequence() {
        ValidationResult validate = ValidationUtils.validate(new UserRequest("TOM", "Son", "2131", ""), UserRequest.UserGroup.class);
        log.info(validate.getMessage());
    }


    @Test
    void testGroupSequenceProvider() {
        ValidationResult validate = ValidationUtils.validate(new ContractRequest("233", new TimeRangeRequest(new Date(System.currentTimeMillis()), new Date(1684134439000L)), 1, "2022-01-21", "2022-01-01"));
        log.info(validate.getMessage());
    }

}

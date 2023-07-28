package com.example.springspelexample.support.annoation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fdrama
 * date 2023年07月26日 16:15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogRecordConfigurationSelector.class)
public @interface EnableLogRecord {

    boolean proxyTargetClass() default false;


    AdviceMode mode() default AdviceMode.PROXY;


    int order() default Ordered.LOWEST_PRECEDENCE;

}

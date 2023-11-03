package com.example.springspelexample.support.annoation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fdrama
 * date 2023年07月27日 9:25
 */
@Repeatable(LogRecords.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogRecord {
    String bizNo() default "";

    /**
     * 日志记录条件 默认记录
     * @return
     */
    String condition() default "";

    /**
     * 日志记录成功条件 默认成功
     * @return
     */
    String successCondition() default "";

    /**
     * 日志记录成功模板
     * @return
     */
    String successTemplate() default "";

    /**
     * 日志记录失败模板
     * @return
     */
    String failTemplate() default "";

    /**
     * 日志记录额外信息
     * @return
     */
    String extra() default "";

    /**
     * 操作人id
     * @return
     */
    String operatorId() default "";

    /**
     * 日志类型
     * @return
     */
    String type() default "";

    /**
     * 日志子类型
     * @return
     */
    String subType() default "";


}

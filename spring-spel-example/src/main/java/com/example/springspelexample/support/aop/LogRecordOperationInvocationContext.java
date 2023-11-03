package com.example.springspelexample.support.aop;

import com.example.springspelexample.support.annoation.LogRecordOperation;

import java.lang.reflect.Method;

/**
 * @author fdrama
 * date 2023年07月26日 18:25
 */
public interface LogRecordOperationInvocationContext<O extends LogRecordOperation> {
    /**
     * 获取目标对象
     *
     * @return
     */
    Object getTarget();

    /**
     * 获取目标方法
     *
     * @return
     */
    Method getMethod();

    /**
     * 获取目标方法参数
     *
     * @return
     */
    Object[] getArgs();

    /**
     * 获取操作
     *
     * @return
     */
    O getOperation();
}

package com.example.springspelexample.support.aop;

import com.example.springspelexample.support.annoation.LogRecordOperation;

import java.lang.reflect.Method;

/**
 * @author fdrama
 * date 2023年07月26日 18:25
 */
public interface LogRecordOperationInvocationContext <O extends LogRecordOperation> {
    Object getTarget();

    Method getMethod();

    Object[] getArgs();

    O getOperation();
}

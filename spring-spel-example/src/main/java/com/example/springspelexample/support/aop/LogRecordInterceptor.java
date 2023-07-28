package com.example.springspelexample.support.aop;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author fdrama
 * date 2023年07月26日 16:12
 */
public class LogRecordInterceptor extends LogRecordAspectSupport implements MethodInterceptor, Serializable {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        LogRecordOperationInvoker aopAllianceInvoker = () -> {
            try {
                return invocation.proceed();
            }
            catch (Throwable ex) {
                throw new LogRecordOperationInvoker.ThrowableWrapper(ex);
            }
        };

        Object target = invocation.getThis();
        Assert.state(target != null, "Target must not be null");
        try {
            return execute(aopAllianceInvoker, target, method, invocation.getArguments());
        }
        catch (CacheOperationInvoker.ThrowableWrapper th) {
            throw th.getOriginal();
        }
    }

}

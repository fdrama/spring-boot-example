package com.example.springspelexample.support.aop;

import com.example.springspelexample.support.annoation.LogRecordOperationSource;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author fdrama
 * date 2023年07月26日 16:30
 */
public abstract class LogRecordOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {


    @Override
    public void setClassFilter(ClassFilter classFilter) {
        setClassFilter(new LogRecordOperationSourceClassFilter());
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        LogRecordOperationSource cas = getLogRecordOperationSource();
        return (cas != null && !CollectionUtils.isEmpty(cas.getLogRecordOperation(method, targetClass)));
    }

    /**
     * Return the cache operation attribute source for this pointcut.
     *
     * @return
     */
    @Nullable
    protected abstract LogRecordOperationSource getLogRecordOperationSource();


    private class LogRecordOperationSourceClassFilter implements ClassFilter {

        @Override
        public boolean matches(Class<?> clazz) {
            LogRecordOperationSource cas = getLogRecordOperationSource();
            return (cas == null || cas.isCandidateClass(clazz));
        }
    }
}

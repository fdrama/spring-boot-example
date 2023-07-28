package com.example.springspelexample.support.annoation;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author fdrama
 * date 2023年07月26日 16:28
 */
public interface LogRecordOperationSource {

    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }


    @Nullable
    Collection<LogRecordOperation> getLogRecordOperation(Method method, @Nullable Class<?> targetClass);
}

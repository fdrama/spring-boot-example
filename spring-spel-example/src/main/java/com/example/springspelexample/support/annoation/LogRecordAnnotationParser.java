package com.example.springspelexample.support.annoation;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author fdrama
 * date 2023年07月27日 9:23
 */
public interface LogRecordAnnotationParser {

    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    @Nullable
    Collection<LogRecordOperation> parseLogRecordAnnotations(Method method);
}

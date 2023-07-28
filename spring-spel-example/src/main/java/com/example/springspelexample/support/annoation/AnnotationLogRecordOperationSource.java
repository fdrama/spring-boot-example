package com.example.springspelexample.support.annoation;

import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author fdrama
 * date 2023年07月26日 16:40
 */
public class AnnotationLogRecordOperationSource extends AbstractFallbackLogRecordOperationSource implements Serializable {


    private final Set<LogRecordAnnotationParser> annotationParsers;

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        for (LogRecordAnnotationParser parser : this.annotationParsers) {
            if (parser.isCandidateClass(targetClass)) {
                return true;
            }
        }
        return false;
    }
    public AnnotationLogRecordOperationSource() {
        this.annotationParsers = Collections.singleton(new SpringLogRecordAnnotationParser());
    }


    @Nullable
    @Override
    protected Collection<LogRecordOperation> findLogRecordOperations(Method method) {
        return determineLogRecordOperations(parser -> parser.parseLogRecordAnnotations(method));
    }


    private Collection<LogRecordOperation> determineLogRecordOperations(LogRecordOperationProvider provider) {
        Collection<LogRecordOperation> ops = null;
        for (LogRecordAnnotationParser annotationParser : this.annotationParsers) {
            Collection<LogRecordOperation> annOps = provider.getLogRecordOperations(annotationParser);
            if (annOps != null) {
                if (ops == null) {
                    ops = annOps;
                }else {
                    Collection<LogRecordOperation> combined = new ArrayList<>(ops.size() + annOps.size());
                    combined.addAll(ops);
                    combined.addAll(annOps);
                    ops = combined;
                }
            }
        }
        return ops;
    }

    @FunctionalInterface
    protected interface LogRecordOperationProvider {

        /**
         * Return the {@link LogRecordOperation} instance(s) provided by the specified parser.
         * @param parser the parser to use
         * @return the cache operations, or {@code null} if none found
         */
        @Nullable
        Collection<LogRecordOperation> getLogRecordOperations(LogRecordAnnotationParser parser);
    }

}

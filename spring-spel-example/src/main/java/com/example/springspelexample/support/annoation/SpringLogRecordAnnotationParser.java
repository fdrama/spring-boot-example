package com.example.springspelexample.support.annoation;

import com.example.springspelexample.support.config.LogRecordConfig;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author fdrama
 * date 2023年07月27日 9:24
 */
public class SpringLogRecordAnnotationParser implements LogRecordAnnotationParser, Serializable {
    private static final Set<Class<? extends Annotation>> OPERATION_ANNOTATIONS = new LinkedHashSet<>(8);

    static {
        OPERATION_ANNOTATIONS.add(LogRecord.class);
        OPERATION_ANNOTATIONS.add(LogRecords.class);
    }

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, OPERATION_ANNOTATIONS);
    }

    @Override
    public Collection<LogRecordOperation> parseLogRecordAnnotations(Method method) {
        DefaultLogRecordConfig defaultConfig = new DefaultLogRecordConfig(method.getDeclaringClass());
        return parseLogRecordAnnotations(defaultConfig, method);
    }

    @Nullable
    private Collection<LogRecordOperation> parseLogRecordAnnotations(DefaultLogRecordConfig defaultLogRecordConfig, AnnotatedElement ae) {
        Collection<LogRecordOperation> ops = parseLogRecordAnnotations(defaultLogRecordConfig, ae, false);
        if (ops != null && ops.size() > 1) {
            // More than one operation found -> local declarations override interface-declared ones...
            Collection<LogRecordOperation> localOps = parseLogRecordAnnotations(defaultLogRecordConfig, ae, true);
            if (localOps != null) {
                return localOps;
            }
        }
        return ops;
    }

    private Collection<LogRecordOperation> parseLogRecordAnnotations(DefaultLogRecordConfig defaultLogRecordConfig, AnnotatedElement ae, boolean localOnly) {
        Collection<? extends Annotation> anns = (localOnly ?
                AnnotatedElementUtils.getAllMergedAnnotations(ae, OPERATION_ANNOTATIONS) :
                AnnotatedElementUtils.findAllMergedAnnotations(ae, OPERATION_ANNOTATIONS));
        if (anns.isEmpty()) {
            return null;
        }
        final Collection<LogRecordOperation> ops = new ArrayList<>(1);
        anns.stream().filter(ann -> ann instanceof LogRecord).forEach(
                ann -> ops.add(parseLogRecordAnnotation(ae, defaultLogRecordConfig, (LogRecord) ann)));

        anns.stream().filter(ann -> ann instanceof LogRecords).forEach(
                logs -> Arrays.stream(((LogRecords) logs).value()).forEach(ann -> {
                    ops.add(parseLogRecordAnnotation(ae, defaultLogRecordConfig, ann));
                })
        );
        return ops;
    }

    private LogRecordOperation parseLogRecordAnnotation(AnnotatedElement ae, DefaultLogRecordConfig defaultLogRecordConfig, LogRecord logRecord) {
        LogRecordOperation.Builder builder = new LogRecordOperation.Builder();
        builder.setName(ae.toString());
        builder.setBizNo(logRecord.bizNo());
        builder.setCondition(logRecord.condition());
        builder.setSuccessCondition(logRecord.successCondition());
        builder.setSuccessTemplate(logRecord.successTemplate());
        builder.setFailTemplate(logRecord.failTemplate());
        builder.setOperator(logRecord.operatorId());
        builder.setType(logRecord.type());
        builder.setSubType(logRecord.subType());
        builder.setExtra(logRecord.extra());
        defaultLogRecordConfig.applyDefault(builder);
        LogRecordOperation op = builder.build();
        validateLogRecordOperation(ae, op);
        return op;
    }

    private void validateLogRecordOperation(AnnotatedElement ae, LogRecordOperation op) {
        if (!StringUtils.hasText(op.getSuccessTemplate()) && !StringUtils.hasText(op.getFailTemplate())) {
            throw new IllegalArgumentException("Invalid log record annotation configuration on '" +
                    ae.toString() + "'. Either 'successTemplate' or 'failTemplate' must be set.");
        }
    }


    private static class DefaultLogRecordConfig {

        private final Class<?> target;

        private String logResolver;

        private String errorHandler;

        public DefaultLogRecordConfig(Class<?> targetClass) {
            this.target = targetClass;
        }

        private boolean initialized = false;

        public void applyDefault(LogRecordOperation.Builder builder) {

            if (!this.initialized) {
                // find target class LogRecordConfig annotation , set logResolver and errorHandler
                LogRecordConfig annotation = AnnotatedElementUtils.findMergedAnnotation(this.target, LogRecordConfig.class);
                if (annotation != null) {
                    this.logResolver = annotation.logResolver();
                    this.errorHandler = annotation.errorHandler();
                }
                this.initialized = true;
            }
            if (builder.getLogResolver().isEmpty() && this.logResolver != null) {
                builder.setLogResolver(this.logResolver);
            }
            if (!StringUtils.hasText(builder.getErrorHandler()) && this.errorHandler != null) {
                builder.setErrorHandler(this.errorHandler);
            }
        }
    }
}

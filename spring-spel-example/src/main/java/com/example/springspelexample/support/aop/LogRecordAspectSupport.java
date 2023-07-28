package com.example.springspelexample.support.aop;


import com.example.springspelexample.support.annoation.LogRecordOperation;
import com.example.springspelexample.support.annoation.LogRecordOperationSource;
import com.example.springspelexample.support.expression.LogRecordOperationExpressionEvaluator;
import com.example.springspelexample.support.expression.LogRecordThreadContext;
import com.example.springspelexample.support.function.IFunctionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fdrama
 * date 2023年07月26日 16:12
 */
public abstract class LogRecordAspectSupport implements BeanFactoryAware, InitializingBean, SmartInitializingSingleton {

    @Nullable
    private LogRecordOperationSource logRecordOperationSource;

    @Nullable
    private BeanFactory beanFactory;

    private boolean initialized = false;

    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private final Map<LogRecordOperationCacheKey, LogRecordOperationMetadata> metadataCache = new ConcurrentHashMap<>(1024);

    private final LogRecordOperationExpressionEvaluator evaluator = new LogRecordOperationExpressionEvaluator();

    private SingletonSupplier<LogRecordResolver> logResolver;

    private SingletonSupplier<LogRecordErrorHandler> errorHandler;

    private IFunctionService functionService;

    private final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    public void configure(
            @Nullable Supplier<LogRecordErrorHandler> errorHandler, @Nullable Supplier<LogRecordResolver> logResolver) {

        this.errorHandler = new SingletonSupplier<>(errorHandler, SimpleLogRecordErrorHandler::new);
        this.logResolver = new SingletonSupplier<>(logResolver, SimpleLogRecordResolver::new);
    }

    public void setLogRecordOperationSource(@Nullable LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }

    @Nullable
    public LogRecordOperationSource getLogRecordOperationSource() {
        return this.logRecordOperationSource;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.state(getLogRecordOperationSource() != null, "The 'logRecordOperationSource' property is required: " +
                "If there are no logRecord methods, then don't use a logRecord aspect.");
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.initialized = true;
        setFunctionService(BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, IFunctionService.class, "functionService"));
    }

    protected Object execute(LogRecordOperationInvoker invoker, Object target, Method method, Object[] args) {
        if (initialized) {
            Class<?> targetClass = getTargetClass(target);
            LogRecordOperationSource operationSource = getLogRecordOperationSource();
            if (operationSource != null) {
                Collection<LogRecordOperation> operations = operationSource.getLogRecordOperation(method, targetClass);
                if (!CollectionUtils.isEmpty(operations)) {
                    return execute(invoker, new LogRecordOperationContexts(operations, method, args, target, targetClass));
                }
            }
        }
        return invoker.invoke();
    }

    private Object execute(LogRecordOperationInvoker invoker, LogRecordOperationContexts contexts) {
        try {
            Object result = invokeOperation(invoker);
            LogRecordThreadContext.putEmptySpan();
            for (LogRecordOperationContext context : contexts.get(LogRecordOperation.class)) {
                if (isConditionPassing(context, result)) {
                    LogRecordOperation operation = context.getOperation();
                    recordExecute(context, operation, result);
                }
            }
            return result;
        } finally {
            LogRecordThreadContext.clear();
        }
    }

    private void recordExecute(LogRecordOperationContext context, LogRecordOperation operation, Object result) {
        List<String> spElTemplates = getSpELTemplates(operation);
        EvaluationContext evaluationContext = evaluator.createEvaluationContext(context.metadata.method, context.args, context.target, context.metadata.targetClass, context.metadata.targetMethod, result, beanFactory);
        Map<String, String> expressionValues = new HashMap<>(spElTemplates.size());
        for (String spElTemplate : spElTemplates) {
            if (spElTemplate.startsWith("{") && spElTemplate.endsWith("}")) {
                AnnotatedElementKey methodKey = context.metadata.methodKey;
                Matcher matcher = TEMPLATE_PATTERN.matcher(spElTemplate);
                StringBuffer parsedStr = new StringBuffer();
                while (matcher.find()) {
                    String functionName = matcher.group(1);
                    String expression = matcher.group(2);
                    Object value = evaluator.getExpression(expression, methodKey, evaluationContext);
                    ;
                    if (StringUtils.hasText(functionName)) {
                        value = functionService.apply(functionName, value);
                    }
                    matcher.appendReplacement(parsedStr, Matcher.quoteReplacement(value == null ? "" : value.toString()));
                }
                matcher.appendTail(parsedStr);
                expressionValues.put(spElTemplate, parsedStr.toString());
            } else {
                expressionValues.put(spElTemplate, spElTemplate);
            }
        }
    }

    private List<String> getSpELTemplates(LogRecordOperation operation) {
        List<String> spElTemplates = new ArrayList<>();
        spElTemplates.add(operation.getType());
        spElTemplates.add(operation.getSubType());
        spElTemplates.add(operation.getCondition());
        spElTemplates.add(operation.getOperator());
        spElTemplates.add(operation.getBizNo());
        spElTemplates.add(operation.getContent());
        spElTemplates.add(operation.getExtra());
        return spElTemplates;
    }

    private boolean isConditionPassing(LogRecordOperationContext context, Object object) {
        boolean passing = context.isConditionPassing(object);
        if (!passing && logger.isTraceEnabled()) {
            logger.trace("log record condition failed on method " + context.metadata.method +
                    " for operation " + context.metadata.operation);
        }
        return passing;
    }


    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }

    @Nullable
    protected Object invokeOperation(LogRecordOperationInvoker invoker) {
        return invoker.invoke();
    }

    private class LogRecordOperationContexts {

        private final MultiValueMap<Class<? extends LogRecordOperation>, LogRecordOperationContext> contexts;


        public LogRecordOperationContexts(Collection<? extends LogRecordOperation> operations, Method method,
                                          Object[] args, Object target, Class<?> targetClass) {

            this.contexts = new LinkedMultiValueMap<>(operations.size());
            for (LogRecordOperation op : operations) {
                this.contexts.add(op.getClass(), getOperationContext(op, method, args, target, targetClass));
            }
        }

        public Collection<LogRecordOperationContext> get(Class<? extends LogRecordOperation> operationClass) {
            Collection<LogRecordOperationContext> result = this.contexts.get(operationClass);
            return (result != null ? result : Collections.emptyList());
        }

    }

    private LogRecordOperationContext getOperationContext(LogRecordOperation operation, Method method, Object[] args, Object target, Class<?> targetClass) {
        LogRecordOperationMetadata metadata = getLogRecordOperationMetadata(operation, method, targetClass);
        return new LogRecordOperationContext(metadata, args, target);
    }

    protected class LogRecordOperationContext implements LogRecordOperationInvocationContext<LogRecordOperation> {
        private final LogRecordOperationMetadata metadata;

        private final Object[] args;

        private final Object target;

        @Nullable
        private Boolean conditionPassing;

        public LogRecordOperationContext(LogRecordOperationMetadata metadata, Object[] args, Object target) {
            this.metadata = metadata;
            this.args = extractArgs(metadata.method, args);
            this.target = target;
        }

        private Object[] extractArgs(Method method, Object[] args) {
            if (!method.isVarArgs()) {
                return args;
            }
            Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
            Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }

        @Override
        public Object getTarget() {
            return this.target;
        }

        @Override
        public Method getMethod() {
            return this.metadata.method;
        }

        @Override
        public Object[] getArgs() {
            return this.args;
        }

        @Override
        public LogRecordOperation getOperation() {
            return this.metadata.operation;
        }

        protected boolean isConditionPassing(Object result) {
            if (this.conditionPassing == null) {
                if (StringUtils.hasText(this.metadata.operation.getCondition())) {
                    EvaluationContext evaluationContext = createEvaluationContext(result);
                    this.conditionPassing = evaluator.condition(this.metadata.operation.getCondition(),
                            this.metadata.methodKey, evaluationContext);
                } else {
                    this.conditionPassing = true;
                }
            }
            return this.conditionPassing;
        }

        private EvaluationContext createEvaluationContext(@Nullable Object result) {
            return evaluator.createEvaluationContext(this.metadata.method, this.args,
                    this.target, this.metadata.targetClass, this.metadata.targetMethod, result, beanFactory);
        }
    }

    protected LogRecordOperationMetadata getLogRecordOperationMetadata(
            LogRecordOperation operation, Method method, Class<?> targetClass) {

        LogRecordOperationCacheKey cacheKey = new LogRecordOperationCacheKey(operation, method, targetClass);
        LogRecordOperationMetadata metadata = this.metadataCache.get(cacheKey);
        if (metadata == null) {

            metadata = new LogRecordOperationMetadata(operation, method, targetClass);
            this.metadataCache.put(cacheKey, metadata);
        }
        return metadata;
    }

    protected static class LogRecordOperationMetadata {

        private final LogRecordOperation operation;

        private final Method method;

        private final Class<?> targetClass;

        private final Method targetMethod;

        private final AnnotatedElementKey methodKey;


        public LogRecordOperationMetadata(LogRecordOperation operation, Method method, Class<?> targetClass) {
            this.operation = operation;
            this.method = BridgeMethodResolver.findBridgedMethod(method);
            this.targetClass = targetClass;
            this.targetMethod = (!Proxy.isProxyClass(targetClass) ?
                    AopUtils.getMostSpecificMethod(method, targetClass) : this.method);
            this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
        }
    }

    private static final class LogRecordOperationCacheKey implements Comparable<LogRecordOperationCacheKey> {

        private final LogRecordOperation recordOperation;

        private final AnnotatedElementKey methodCacheKey;

        private LogRecordOperationCacheKey(LogRecordOperation recordOperation, Method method, Class<?> targetClass) {
            this.recordOperation = recordOperation;
            this.methodCacheKey = new AnnotatedElementKey(method, targetClass);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LogRecordOperationCacheKey)) {
                return false;
            }
            LogRecordOperationCacheKey otherKey = (LogRecordOperationCacheKey) other;
            return (this.recordOperation.equals(otherKey.recordOperation) &&
                    this.methodCacheKey.equals(otherKey.methodCacheKey));
        }

        @Override
        public int hashCode() {
            return (this.recordOperation.hashCode() * 31 + this.methodCacheKey.hashCode());
        }

        @Override
        public String toString() {
            return this.recordOperation + " on " + this.methodCacheKey;
        }

        @Override
        public int compareTo(LogRecordOperationCacheKey other) {
            int result = this.recordOperation.toString().compareTo(other.recordOperation.toString());
            if (result == 0) {
                result = this.methodCacheKey.compareTo(other.methodCacheKey);
            }
            return result;
        }
    }

    protected <T> T getBean(String beanName, Class<T> expectedType) {
        if (this.beanFactory == null) {
            throw new IllegalStateException(
                    "BeanFactory must be set on cache aspect for " + expectedType.getSimpleName() + " retrieval");
        }
        return BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, expectedType, beanName);
    }

    /**
     * Clear the cached metadata.
     */
    protected void clearMetadataCache() {
        this.metadataCache.clear();
        // this.evaluator.clear();
    }

    public void setFunctionService(IFunctionService functionService) {
        this.functionService = functionService;
    }
}

package com.example.springspelexample.support.aop;


import com.example.springspelexample.support.annoation.LogRecordOperation;
import com.example.springspelexample.support.annoation.LogRecordOperationSource;
import com.example.springspelexample.support.config.LogRecordDetail;
import com.example.springspelexample.support.config.LogRecordErrorHandler;
import com.example.springspelexample.support.config.LogRecordResolver;
import com.example.springspelexample.support.config.SimpleLogRecordErrorHandler;
import com.example.springspelexample.support.config.SimpleLogRecordResolver;
import com.example.springspelexample.support.expression.LogRecordOperationExpressionEvaluator;
import com.example.springspelexample.support.expression.LogRecordThreadContext;
import com.example.springspelexample.support.function.IFunctionService;
import com.example.springspelexample.support.function.IOperatorGetService;
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
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.SupplierUtils;

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

    public static final String COMMA = ",";
    private LogRecordOperationSource logRecordOperationSource;

    private BeanFactory beanFactory;

    private boolean initialized = false;

    protected final Log logger = LogFactory.getLog(getClass());

    private final Map<LogRecordOperationCacheKey, LogRecordOperationMetadata> metadataCache = new ConcurrentHashMap<>(1024);

    private final LogRecordOperationExpressionEvaluator evaluator = new LogRecordOperationExpressionEvaluator();

    private SingletonSupplier<LogRecordResolver> recordResolver;

    private SingletonSupplier<LogRecordErrorHandler> errorHandler;

    private IFunctionService functionService;

    private IOperatorGetService operatorGetService;
    private final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    public void configure(
            @Nullable Supplier<LogRecordErrorHandler> errorHandler, @Nullable Supplier<LogRecordResolver> logResolver) {
        this.errorHandler = new SingletonSupplier<>(errorHandler, SimpleLogRecordErrorHandler::new);
        this.recordResolver = new SingletonSupplier<>(logResolver, SimpleLogRecordResolver::new);
    }

    public void setLogRecordOperationSource(LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }

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
        setOperatorGetService(BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, IOperatorGetService.class, "operatorGetService"));
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
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            long startTime = System.currentTimeMillis();
            Object result = null;
            Throwable e = null;
            String errorMsg = null;
            try {
                result = invokeOperation(invoker);
            } catch (Throwable throwable) {
                e = throwable;
                errorMsg = throwable.getMessage();
            }
            long endTime = System.currentTimeMillis();

            for (LogRecordOperationContext context : contexts.get(LogRecordOperation.class)) {
                recordExecute(context, new MethodExecuteContext(startTime, endTime, result, e, errorMsg));
            }
            stopWatch.stop();
            if (logger.isDebugEnabled()) {
                logger.debug("LogRecordAspectSupport execute time: " + stopWatch.getTotalTimeMillis() + " ms");
            }
            return result;
        } finally {
            LogRecordThreadContext.clear();
        }
    }

    private void recordExecute(LogRecordOperationContext operationContext, MethodExecuteContext methodExecuteContext) {
        try {
            EvaluationContext evaluationContext = evaluator.createEvaluationContext(operationContext.metadata.method, operationContext.args, operationContext.target, operationContext.metadata.targetClass, operationContext.metadata.targetMethod, methodExecuteContext.getResult(), methodExecuteContext.getErrorMsg(), beanFactory);
            if (!isConditionPass(operationContext, evaluationContext)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("log record condition failed on method " + operationContext.metadata.method +
                            " for operation " + operationContext.metadata.operation);
                }
                return;
            }
            String operatorId = getOperatorId(operationContext, evaluationContext);
            if (!StringUtils.hasText(operatorId)) {
                throw new IllegalArgumentException("log record operatorId is empty on method " + operationContext.metadata.method +
                        " for operation " + operationContext.metadata.operation);
            }
            List<String> spElTemplates = getSpELTemplates(operationContext.metadata.operation);
            Map<String, String> expressionValues = new HashMap<>(spElTemplates.size());
            Map<String, String> functionValues = new HashMap<>(spElTemplates.size());
            for (String spElTemplate : spElTemplates) {
                if (spElTemplate.contains("{") && spElTemplate.contains("}")) {
                    AnnotatedElementKey methodKey = operationContext.metadata.methodKey;
                    Matcher matcher = TEMPLATE_PATTERN.matcher(spElTemplate);
                    StringBuffer parsedStr = new StringBuffer();
                    while (matcher.find()) {
                        String functionName = matcher.group(1);
                        String expression = matcher.group(2);
                        String expressionValue;
                        if (StringUtils.hasText(functionName)) {
                            expressionValue = nullToEmpty(getFunctionValue(functionName, expression, functionValues, methodKey, evaluationContext));
                        } else {
                            expressionValue = nullToEmpty(evaluator.getExpressionValue(expression, methodKey, evaluationContext));
                        }
                        matcher.appendReplacement(parsedStr, Matcher.quoteReplacement(expressionValue == null ? "" : expressionValue));
                    }
                    matcher.appendTail(parsedStr);
                    expressionValues.put(spElTemplate, parsedStr.toString());
                } else {
                    expressionValues.put(spElTemplate, spElTemplate);
                }
            }
            Boolean successCondition = getSuccessCondition(operationContext, evaluationContext, methodExecuteContext);
            resolveLogRecord(operationContext, expressionValues, methodExecuteContext, successCondition, operatorId);
        } catch (RuntimeException e) {
            logger.error("LogRecordAspectSupport recordExecute error", e);
            if (operationContext.metadata.errorHandler != null) {
                operationContext.metadata.errorHandler.handleLogRecordError(operationContext, e);
            }
        }

    }

    private String getOperatorId(LogRecordOperationContext operationContext, EvaluationContext evaluationContext) {

        if (StringUtils.hasText(operationContext.metadata.operation.getOperator())) {
            return nullToEmpty(evaluator.getExpressionValue(operationContext.metadata.operation.getOperator(),
                    operationContext.metadata.methodKey, evaluationContext));
        } else {
            if (operatorGetService == null) {
                return null;
            } else {
                return operatorGetService.get();
            }
        }
    }

    private Boolean getSuccessCondition(LogRecordOperationContext operationContext, EvaluationContext evaluationContext, MethodExecuteContext methodExecuteContext) {
        if (!StringUtils.hasText(operationContext.metadata.operation.getSuccessCondition())) {
            return methodExecuteContext.getThrowable() == null;
        } else {
            return evaluator.condition(operationContext.metadata.operation.getSuccessCondition(),
                    operationContext.metadata.methodKey, evaluationContext);
        }
    }

    private Boolean isConditionPass(LogRecordOperationContext operationContext, EvaluationContext evaluationContext) {
        if (!StringUtils.hasText(operationContext.metadata.operation.getCondition())) {
            return true;
        }
        return evaluator.condition(operationContext.metadata.operation.getCondition(),
                operationContext.metadata.methodKey, evaluationContext);
    }


    private String nullToEmpty(Object object) {
        return object == null ? "" : object.toString();
    }

    private String getFunctionValue(String functionName, String expression, Map<String, String> functionValues, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        String[] expressionArray;
        if (expression.contains(COMMA)) {
            expressionArray = expression.split(COMMA);
        } else {
            expressionArray = new String[]{expression};
        }
        List<Object> args = new ArrayList<>(expressionArray.length);
        for (String ex : expressionArray) {
            if (!StringUtils.hasText(ex)) {
                args.add(ex);
            } else {
                Object arg = evaluator.getExpressionValue(ex, methodKey, evalContext);
                args.add(arg);
            }
        }
        String functionReturnValue;
        String functionCallInstanceKey = getFunctionCallInstanceKey(functionName, expression);
        if (functionValues != null && functionValues.containsKey(functionCallInstanceKey)) {
            functionReturnValue = functionValues.get(functionCallInstanceKey);
        } else {
            functionReturnValue = functionService.apply(functionName, args.toArray());
        }
        return functionReturnValue;
    }

    private String getFunctionCallInstanceKey(String functionName, String expression) {
        return functionName + expression;
    }

    private void resolveLogRecord(LogRecordOperationContext context, Map<String, String> expressionValues, MethodExecuteContext methodExecuteContext, Boolean successCondition, String operatorId) {
        boolean methodSuccess = methodExecuteContext.throwable == null;
        LogRecordOperation operation = context.metadata.operation;
        if (successCondition && !StringUtils.hasText(operation.getSuccessTemplate())) {
            logger.warn("LogRecordAspectSupport saveLogRecordDetail successCondition is true but successTemplate is empty");
            return;
        }
        if (!successCondition && !StringUtils.hasText(operation.getFailTemplate())) {
            logger.warn("LogRecordAspectSupport saveLogRecordDetail successCondition is false but failTemplate is empty");
            return;
        }
        LogRecordDetail recordDetail = LogRecordDetail.builder()
                .bizNo(expressionValues.get(operation.getBizNo()))
                .extra(expressionValues.get(operation.getExtra()))
                .content(expressionValues.get(successCondition ? operation.getSuccessTemplate() : operation.getFailTemplate()))
                .type(expressionValues.get(operation.getType()))
                .subType(expressionValues.get(operation.getSubType()))
                .methodName(context.metadata.method.getName())
                .className(context.metadata.targetClass.getName())
                .operatorId(operatorId)
                .operateTimeBegin(methodExecuteContext.getStartTime())
                .operateTimeEnd(methodExecuteContext.getEndTime())
                .methodSuccess(methodSuccess)
                .build();
        LogRecordResolver logRecordResolver = context.metadata.recordResolver;
        logRecordResolver.resolveLog(recordDetail);
    }

    private List<String> getSpELTemplates(LogRecordOperation operation) {
        List<String> spElTemplates = new ArrayList<>();
        spElTemplates.add(operation.getType());
        spElTemplates.add(operation.getSubType());
        spElTemplates.add(operation.getCondition());
        spElTemplates.add(operation.getSuccessCondition());
        spElTemplates.add(operation.getBizNo());
        spElTemplates.add(operation.getSuccessTemplate());
        spElTemplates.add(operation.getFailTemplate());
        spElTemplates.add(operation.getExtra());
        return spElTemplates;
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

    private static class MethodExecuteContext {
        private final long startTime;
        private final long endTime;
        private final Object result;
        private final Throwable throwable;

        private final String errorMsg;

        public MethodExecuteContext(long startTime, long endTime, Object result, Throwable throwable, String errorMsg) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.result = result;
            this.throwable = throwable;
            this.errorMsg = errorMsg;
        }


        public long getStartTime() {
            return startTime;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public long getEndTime() {
            return endTime;
        }

        public Object getResult() {
            return result;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

    protected static class LogRecordOperationContext implements LogRecordOperationInvocationContext<LogRecordOperation> {
        private final LogRecordOperationMetadata metadata;

        private final Object[] args;

        private final Object target;


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
    }

    protected LogRecordOperationMetadata getLogRecordOperationMetadata(
            LogRecordOperation operation, Method method, Class<?> targetClass) {

        LogRecordOperationCacheKey cacheKey = new LogRecordOperationCacheKey(operation, method, targetClass);
        LogRecordOperationMetadata metadata = this.metadataCache.get(cacheKey);
        if (metadata == null) {
            LogRecordErrorHandler operationErrorHandler;
            if (StringUtils.hasText(operation.getErrorHandler())) {
                operationErrorHandler = getBean(operation.getErrorHandler(), LogRecordErrorHandler.class);
            } else {
                operationErrorHandler = SupplierUtils.resolve(this.errorHandler);
            }
            LogRecordResolver operationRecordResolver;
            if (StringUtils.hasText(operation.getLogResolver())) {
                operationRecordResolver = getBean(operation.getLogResolver(), LogRecordResolver.class);
            } else {
                operationRecordResolver = SupplierUtils.resolve(this.recordResolver);
            }
            metadata = new LogRecordOperationMetadata(operation, method, targetClass, operationErrorHandler, operationRecordResolver);
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

        private final LogRecordErrorHandler errorHandler;

        private final LogRecordResolver recordResolver;

        public LogRecordOperationMetadata(LogRecordOperation operation, Method method, Class<?> targetClass, LogRecordErrorHandler errorHandler, LogRecordResolver recordResolver) {
            this.operation = operation;
            this.method = BridgeMethodResolver.findBridgedMethod(method);
            this.targetClass = targetClass;
            this.targetMethod = (!Proxy.isProxyClass(targetClass) ?
                    AopUtils.getMostSpecificMethod(method, targetClass) : this.method);
            this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
            this.errorHandler = errorHandler;
            this.recordResolver = recordResolver;
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
        this.evaluator.clear();
    }

    public void setFunctionService(IFunctionService functionService) {
        this.functionService = functionService;
    }

    public void setOperatorGetService(IOperatorGetService operatorGetService) {
        this.operatorGetService = operatorGetService;
    }
}

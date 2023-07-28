package com.example.springspelexample.support.annoation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fdrama
 * date 2023年07月27日 10:33
 */
public abstract class AbstractFallbackLogRecordOperationSource implements LogRecordOperationSource{
    /**
     * Canonical value held in cache to indicate no caching attribute was
     * found for this method and we don't need to look again.
     */
    private static final Collection<LogRecordOperation> NULL_CACHING_ATTRIBUTE = Collections.emptyList();

    private final Map<Object, Collection<LogRecordOperation>> attributeCache = new ConcurrentHashMap<>(1024);
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    @Override
    public Collection<LogRecordOperation> getLogRecordOperation(Method method, Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }

        Object cacheKey = getCacheKey(method, targetClass);
        Collection<LogRecordOperation> cached = this.attributeCache.get(cacheKey);

        if (cached != null) {
            return (cached != NULL_CACHING_ATTRIBUTE ? cached : null);
        }
        else {
            Collection<LogRecordOperation> cacheOps = computeLogRecordOperations(method, targetClass);
            if (cacheOps != null && cacheOps.size() > 0) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Adding cacheable method '" + method.getName() + "' with attribute: " + cacheOps);
                }
                this.attributeCache.put(cacheKey, cacheOps);
            }
            else {
                this.attributeCache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
            }
            return cacheOps;
        }
    }

    private Collection<LogRecordOperation> computeLogRecordOperations(Method method, Class<?> targetClass) {
        // Don't allow non-public methods,
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        // First try is the method in the target class.
        Collection<LogRecordOperation> opDef = findLogRecordOperations(specificMethod);
        if (opDef != null) {
            return opDef;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            opDef = findLogRecordOperations(method);
            return opDef;
        }

        return null;
    }

    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    /**
     * Subclasses need to implement this to return the caching attribute for the
     * given method, if any.
     * @param method the method to retrieve the attribute for
     * @return all caching attribute associated with this method, or {@code null} if none
     */
    @Nullable
    protected abstract Collection<LogRecordOperation> findLogRecordOperations(Method method);
}

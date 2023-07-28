package com.example.springspelexample.support.expression;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author fdrama
 * date 2023年07月27日 11:10
 */
public class LogRecordEvaluationContext extends MethodBasedEvaluationContext {

    private final Set<String> unavailableVariables = new HashSet<>(1);


    public LogRecordEvaluationContext(Object rootObject, Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
        Map<String, Object> variables = LogRecordThreadContext.getVariables();
        Map<String, Object> globalVariables = LogRecordThreadContext.getGlobalVariables();
        if (variables != null) {
            setVariables(variables);
        }
        if (globalVariables != null && !globalVariables.isEmpty()) {
            for (Map.Entry<String, Object> entry : globalVariables.entrySet()) {
                if (lookupVariable(entry.getKey()) == null) {
                    setVariable(entry.getKey(), entry.getValue());
                }
            }
        }
    }


    public void addUnavailableVariable(String name) {
        this.unavailableVariables.add(name);
    }


    /**
     * Load the param information only when needed.
     */
    @Override
    @Nullable
    public Object lookupVariable(String name) {
        if (this.unavailableVariables.contains(name)) {
            throw new EvaluationException("Variable not available");
        }
        return super.lookupVariable(name);
    }
}

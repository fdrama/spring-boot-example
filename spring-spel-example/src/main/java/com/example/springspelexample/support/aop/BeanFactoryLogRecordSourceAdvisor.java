package com.example.springspelexample.support.aop;

import com.example.springspelexample.support.annoation.LogRecordOperationSource;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * @author fdrama
 * date 2023年07月26日 16:14
 */
public class BeanFactoryLogRecordSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {


    private LogRecordOperationSource logRecordOperationSource;

    private final LogRecordOperationSourcePointcut pointcut = new LogRecordOperationSourcePointcut() {
        @Override
        protected LogRecordOperationSource getLogRecordOperationSource() {
            return logRecordOperationSource;
        }
    };


    /**
     * Set the cache operation attribute source which is used to find cache
     * attributes. This should usually be identical to the source reference
     * set on the cache interceptor itself.
     */
    public void setLogRecordOperationSource(LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }

    /**
     * Set the {@link ClassFilter} to use for this pointcut.
     * Default is {@link ClassFilter#TRUE}.
     */
    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}

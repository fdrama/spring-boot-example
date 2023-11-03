package com.example.springspelexample.support.annoation;

import com.example.springspelexample.support.aop.BeanFactoryLogRecordSourceAdvisor;
import com.example.springspelexample.support.aop.LogRecordInterceptor;
import com.example.springspelexample.support.config.LogRecordErrorHandler;
import com.example.springspelexample.support.config.LogRecordResolver;
import com.example.springspelexample.support.function.DefaultFunctionServiceImpl;
import com.example.springspelexample.support.function.DefaultOperatorGetServiceImpl;
import com.example.springspelexample.support.function.DefaultParseFunction;
import com.example.springspelexample.support.function.IFunctionService;
import com.example.springspelexample.support.function.IOperatorGetService;
import com.example.springspelexample.support.function.IParseFunction;
import com.example.springspelexample.support.function.ParseFunctionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author fdrama
 * date 2023年07月26日 16:20
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyLogRecordConfiguration implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableLogRecord;

    @Nullable
    protected Supplier<LogRecordResolver> logResolver;

    @Nullable
    protected Supplier<LogRecordErrorHandler> errorHandler;

    public static final String ORDER = "order";

    @Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryLogRecordSourceAdvisor cacheAdvisor(
            LogRecordOperationSource logRecordOperationSource, LogRecordInterceptor logRecordInterceptor) {

        BeanFactoryLogRecordSourceAdvisor advisor = new BeanFactoryLogRecordSourceAdvisor();
        advisor.setLogRecordOperationSource(logRecordOperationSource);
        advisor.setAdvice(logRecordInterceptor);
        if (this.enableLogRecord != null) {
            advisor.setOrder(this.enableLogRecord.<Integer>getNumber(ORDER));
        }
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordOperationSource logRecordOperationSource() {
        return new AnnotationLogRecordOperationSource();
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordInterceptor logRecordInterceptor(LogRecordOperationSource logRecordOperationSource) {
        LogRecordInterceptor interceptor = new LogRecordInterceptor();
        interceptor.configure(this.errorHandler, this.logResolver);
        interceptor.setLogRecordOperationSource(logRecordOperationSource);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(IFunctionService.class)
    public IFunctionService functionService(ParseFunctionFactory parseFunctionFactory) {
        return new DefaultFunctionServiceImpl(parseFunctionFactory);
    }

    @Bean
    public ParseFunctionFactory parseFunctionFactory(@Autowired List<IParseFunction> parseFunctions) {
        return new ParseFunctionFactory(parseFunctions);
    }

    @Bean
    @ConditionalOnMissingBean(IParseFunction.class)
    public DefaultParseFunction parseFunction() {
        return new DefaultParseFunction();
    }


    @Bean
    @ConditionalOnMissingBean(IOperatorGetService.class)
    public DefaultOperatorGetServiceImpl operatorGetService() {
        return new DefaultOperatorGetServiceImpl();
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableLogRecord = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableLogRecord.class.getName()));
        if (this.enableLogRecord == null) {
            throw new IllegalArgumentException(
                    "@EnableLogRecord is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required = false)
    void setConfigurers(ObjectProvider<LogRecordConfigurer> configurers) {
        Supplier<LogRecordConfigurer> configurer = () -> {
            List<LogRecordConfigurer> candidates = configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException(candidates.size() + " implementations of " +
                        "LogRecordConfigurer were found when only 1 was expected. " +
                        "Refactor the configuration such that LogRecordConfigurer is " +
                        "implemented only once or not at all.");
            }
            return candidates.get(0);
        };
        useConfigurer(new LogRecordConfigurerSupplier(configurer));
    }

    protected void useConfigurer(LogRecordConfigurerSupplier logRecordConfigurerSupplier) {
        this.logResolver = logRecordConfigurerSupplier.adapt(LogRecordConfigurer::logResolver);
        this.errorHandler = logRecordConfigurerSupplier.adapt(LogRecordConfigurer::errorHandler);
    }

    protected static class LogRecordConfigurerSupplier {

        private final Supplier<LogRecordConfigurer> supplier;

        public LogRecordConfigurerSupplier(Supplier<LogRecordConfigurer> supplier) {
            this.supplier = SingletonSupplier.of(supplier);
        }

        /**
         * Adapt the {@link LogRecordConfigurer} supplier to another supplier
         * provided by the specified mapping function. If the underlying
         * {@link LogRecordConfigurer} is {@code null}, {@code null} is returned
         * and the mapping function is not invoked.
         *
         * @param provider the provider to use to adapt the supplier
         * @param <T>      the type of the supplier
         * @return another supplier mapped by the specified function
         */
        @Nullable
        public <T> Supplier<T> adapt(Function<LogRecordConfigurer, T> provider) {
            return () -> {
                LogRecordConfigurer logRecordConfigurer = this.supplier.get();
                return (logRecordConfigurer != null ? provider.apply(logRecordConfigurer) : null);
            };
        }

    }

}

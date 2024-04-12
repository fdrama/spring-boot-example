package com.example.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigCenterConfigurer extends PropertySourcesPlaceholderConfigurer implements BeanFactoryAware {

    private BeanFactory beanFactory;

    private String beanName;

    private final Map<String, Set<BeanAttr>> autoUpdateBeanMap = new ConcurrentHashMap<>(64);

    private final ConfigCenterRunTimeConfig runTimeConfig = new ConfigCenterRunTimeConfig();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {


        if (!autoUpdateBeanMap.isEmpty()) {
            ConfigAutoUpdateListener autoUpdateListener = new ConfigAutoUpdateListener(autoUpdateBeanMap, beanFactory);
            // 注册配置变更监听器
            runTimeConfig.registerConfigChangeListener(autoUpdateListener);
        }
        // 启动配置更新监听
        ConfigFetchWorker configFetchWorker = new ConfigFetchWorker(runTimeConfig);
        configFetchWorker.start();
    }

    private void registerConfigChangeListener(ConfigChangeListener configChangeListener) {

    }


    protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                       StringValueResolver valueResolver) {

        BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

        String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
        for (String curName : beanNames) {
            // Check that we're not parsing our own bean definition,
            // to avoid failing on unresolvable placeholders in properties file locations.
            if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
                BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
                try {
                    Class<?> beanClass = ((AbstractBeanDefinition) bd).resolveBeanClass(Thread.currentThread().getContextClassLoader());
                    if (beanClass != null) {
                        // 将需要更新的bean字段信息保存到autoUpdateBeanMap
                        recursiveBeanFields(beanClass, curName);
                    }
                    visitor.visitBeanDefinition(bd);
                } catch (Exception ex) {
                    throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
                }
            }
        }

        // New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
        beanFactoryToProcess.resolveAliases(valueResolver);

        // New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
        beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
    }


    private void recursiveBeanFields(Class<?> beanClass, String beanName) {
        boolean isFactoryBean = FactoryBean.class.isAssignableFrom(beanClass);

        Field[] fields = beanClass.getDeclaredFields();
        if (fields.length == 0) {
            return;
        }

        for (Field f : fields) {
            if (f.isAnnotationPresent(AutoUpdate.class) && f.isAnnotationPresent(Value.class)) {
                String key = f.getAnnotation(Value.class).value();
                if (StringUtils.startsWithIgnoreCase(key, placeholderPrefix) && StringUtils.endsWithIgnoreCase(key, placeholderSuffix)) {
                    String actualPlaceholder;
                    int separatorIndex = key.indexOf(valueSeparator);
                    if (separatorIndex != -1) {
                        actualPlaceholder = key.substring(key.indexOf(placeholderPrefix) + placeholderPrefix.length(), separatorIndex);
                    } else {
                        actualPlaceholder = key.substring(key.indexOf(placeholderPrefix) + placeholderPrefix.length(), key.indexOf
                                (placeholderSuffix));
                    }

                    Set<BeanAttr> beanAttrSet = autoUpdateBeanMap.computeIfAbsent(actualPlaceholder, k -> new HashSet<>());
                    beanAttrSet.add(new BeanAttr(beanName, f.getName(), isFactoryBean));
                }
            }
        }

        // 递归处理父类的字段
        if (beanClass.getSuperclass() != null && !Object.class.equals(beanClass.getSuperclass())) {
            recursiveBeanFields(beanClass.getSuperclass(), beanName);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        super.setBeanFactory(beanFactory);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
        super.setBeanName(beanName);
    }

    public Map<String, Set<BeanAttr>> getAutoUpdateBeanMap() {
        return autoUpdateBeanMap;
    }
}

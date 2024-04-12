package com.example.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.BeanFactory;

import java.util.Map;
import java.util.Set;

public class ConfigAutoUpdateListener implements ConfigChangeListener {

    private Map<String, Set<BeanAttr>> autoUpdateBeanMap;

    private BeanFactory beanFactory;

    private Logger logger = LoggerFactory.getLogger(ConfigAutoUpdateListener.class);

    public ConfigAutoUpdateListener(Map<String, Set<BeanAttr>> autoUpdateBeanMap, BeanFactory beanFactory) {
        this.autoUpdateBeanMap = autoUpdateBeanMap;
        this.beanFactory = beanFactory;
    }


    @Override
    public void onChange(ConfigChangedEvent configChangedEvent) {
        ConfigDiff configDiff = configChangedEvent.getConfigDiff();
        if (!configDiff.hasDiff()) {
            return;
        }
        for (ConfigChangedItem configItem : configDiff.getList()) {
            updateBean(configItem);
        }

    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    private void updateBean(ConfigChangedItem configItem) {
        Set<BeanAttr> beanAttrs = autoUpdateBeanMap.get(configItem.getKey());
        if (beanAttrs == null) {
            return;
        }
        for (BeanAttr attr : beanAttrs) {
            String beanName = attr.getBeanName();
            if (attr.isFactoryBean()) {
                beanName = "&" + beanName;
            }
            BeanWrapperImpl wrapper = new BeanWrapperImpl(beanFactory.getBean(beanName));
            wrapper.setPropertyValue(attr.getFiledName(), configItem.getCurrentValue());
            logger.info("update bean [{}] filed [{}]", attr.getBeanName(), attr.getFiledName());
        }

    }
}

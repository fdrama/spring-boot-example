package com.example.support;

public interface ConfigChangeListener {
    /**
     * 配置变更事件
     *
     * @param configChangedEvent
     */
    void onChange(ConfigChangedEvent configChangedEvent);

    /**
     * 顺序
     *
     * @return
     */
    int getOrder();
}

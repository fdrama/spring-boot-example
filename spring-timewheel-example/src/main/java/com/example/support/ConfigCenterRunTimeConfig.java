package com.example.support;

import java.util.ArrayList;
import java.util.List;

public class ConfigCenterRunTimeConfig {

    private ConfigCenterInfo configCenterInfo;
    public List<ConfigChangeListener> changeListeners = new ArrayList<>();


    public ConfigCenterInfo getConfigCenterInfo() {
        return configCenterInfo;
    }

    public void registerConfigChangeListener(ConfigChangeListener configChangeListener) {
        if (configChangeListener != null && !changeListeners.contains(configChangeListener)) {
            changeListeners.add(configChangeListener);
        }
    }
}

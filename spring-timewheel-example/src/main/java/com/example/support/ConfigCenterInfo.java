package com.example.support;

public class ConfigCenterInfo {

    /**
     * 应用
     */
    private final String application;
    /**
     * 模块
     */
    private final String module;

    /**
     * version
     */
    private final String version;

    /**
     * 环境
     */
    private final String env;

    /**
     * mode
     */
    private final String mode;

    public ConfigCenterInfo(String application, String module, String version, String env, String mode) {
        this.application = application;
        this.module = module;
        this.version = version;
        this.env = env;
        this.mode = mode;
    }

    public String getApplication() {
        return application;
    }

    public String getModule() {
        return module;
    }

    public String getVersion() {
        return version;
    }

    public String getEnv() {
        return env;
    }

    public String getMode() {
        return mode;
    }
}

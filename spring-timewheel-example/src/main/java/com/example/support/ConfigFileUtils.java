package com.example.support;

import org.springframework.util.StringUtils;

import java.io.File;

public class ConfigFileUtils {

    static String CONFIG_FILE = "config.json";

    static String DEFAULT_VERSION = "default";

    public static String getLocalCacheFile(ConfigCenterInfo configCenterInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append(getLocalCacheFileDir(configCenterInfo));
        builder.append(File.separator);
        builder.append(CONFIG_FILE);
        return builder.toString();
    }

    public static String getLocalCacheFileDir(ConfigCenterInfo configInfo) {

        StringBuilder builder = new StringBuilder();
        builder.append(ConfigEnv.envConfigDir);
        builder.append(File.separator);
        builder.append(configInfo.getApplication());
        builder.append(File.separator);
        builder.append(configInfo.getModule());
        builder.append(File.separator);

        if (StringUtils.hasText(configInfo.getVersion())) {
            builder.append(configInfo.getVersion());
        } else {
            builder.append(DEFAULT_VERSION);
        }

        return builder.toString();
    }

}

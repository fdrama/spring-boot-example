package com.example.support;

public class ConfigEnv {

    public static final String CONFIG_CENTER_DIR = "CONFIG_CENTER_DIR";
    public static final String CONFIG_CENTER_DIR1 = "config.center.dir";
    public static final String CONFIG_CENTER_JAVA = "/config_center/java";
    public static final String USER_HOME = "user.home";

    public static String envConfigDir;

    static {
        envConfigDir = initConfigDir();
    }

    private static String initConfigDir() {

        String configDir = System.getProperty(CONFIG_CENTER_DIR1);
        if (configDir == null) {
            configDir = System.getenv(CONFIG_CENTER_DIR);
        }
        if (configDir == null) {
            configDir = System.getProperty(USER_HOME) + CONFIG_CENTER_JAVA;
        }
        return configDir;
    }
}

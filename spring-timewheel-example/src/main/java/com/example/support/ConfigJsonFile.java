package com.example.support;

public class ConfigJsonFile {

    private String cacheFile;

    public ConfigJsonFile(String cacheFilePath) {
        this.cacheFile = cacheFilePath;
    }

    public synchronized ConfigObject readCache() {
        return null;
    }
}

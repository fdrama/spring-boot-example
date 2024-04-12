package com.example.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConfigFetchWorker {

    private static final Logger logger = LoggerFactory.getLogger(ConfigFetchWorker.class);


    private volatile Boolean started = false;

    private final ConfigCenterRunTimeConfig runTimeConfig;

    /**
     * 本地文件最后修改时间
     */
    private AtomicLong localFileLastModified = new AtomicLong(0L);


    public ConfigFetchWorker(ConfigCenterRunTimeConfig runTimeConfig) {
        this.runTimeConfig = runTimeConfig;
    }

    private static ScheduledExecutorService remoteScheduledExecutorService;

    private static ScheduledExecutorService localScheduledExecutorService;

    public void start() {
        if (started) {
            return;
        }

        ConfigCenterInfo configCenterInfo = runTimeConfig.getConfigCenterInfo();


        if (remoteScheduledExecutorService == null) {

            remoteScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("ccR-" + getConfigInfoKey(configCenterInfo), true));
        }

        if (localScheduledExecutorService == null) {
            localScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("ccL-" + getConfigInfoKey(configCenterInfo), true));
        }

        // 检测远程配置变更情况
        remoteScheduledExecutorService.scheduleWithFixedDelay(() -> {
                    try {
                        checkRemoteConfigUpdate();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }

                }, 30, 30,
                TimeUnit.SECONDS);

        // 检测本地配置缓存变更情况
        localScheduledExecutorService.scheduleWithFixedDelay(() -> {
                    try {
                        checkLocalConfigUpdate();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }, 10, 10,
                TimeUnit.SECONDS);

        started = true;
    }

    private void checkLocalConfigUpdate() {


    }

    private void checkRemoteConfigUpdate() {
        String file = ConfigFileUtils.getLocalCacheFile(runTimeConfig.getConfigCenterInfo());
        File localFile = new File(file);
        if (!localFile.exists()) {
            logger.info("本地配置文件不存在:{}", file);
            return;
        }

        long lastModified = localFile.lastModified();
        if (lastModified > localFileLastModified.get()) {
            reload(file);
        }
    }

    private void reload(String file) {

        ConfigJsonFile configJsonFile = new ConfigJsonFile(file);


        localFileLastModified.set(System.currentTimeMillis());
    }


    private String getConfigInfoKey(ConfigCenterInfo configCenterInfo) {
        return String.format("%s-%s-%s-%s-%s", configCenterInfo.getApplication(),
                configCenterInfo.getModule(), configCenterInfo.getVersion(),
                configCenterInfo.getEnv(), configCenterInfo.getMode());
    }


    static class NamedThreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        private final String mPrefix;

        private final boolean daemon;

        private final ThreadGroup mGroup;

        public NamedThreadFactory(String prefix, boolean daemon) {
            this.mPrefix = prefix + "-thread-";
            this.daemon = daemon;
            SecurityManager s = System.getSecurityManager();
            this.mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        }

        public Thread newThread(Runnable runnable) {
            String name = mPrefix + mThreadNum.getAndIncrement();
            Thread ret = new Thread(mGroup, runnable, name, 0);
            ret.setDaemon(this.daemon);
            return ret;
        }

        public ThreadGroup getThreadGroup() {
            return mGroup;
        }
    }
}

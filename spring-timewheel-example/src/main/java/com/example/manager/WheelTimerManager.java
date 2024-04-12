package com.example.manager;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WheelTimerManager {

    protected volatile boolean started = false;

    public static HashedWheelTimer wheelTimer = null;

    /**
     * 时间轮每一格的时间间隔
     */
    public static long tickDuration = 100L;

    /**
     * 时间轮的格子数
     */
    public static int ticksPerWheel = 512;


    private static ConcurrentHashMap<String, ConcurrentHashMap<String, Timeout>> timeoutHashMap = new ConcurrentHashMap<>();


    public synchronized void start() throws Exception {
        if (wheelTimer == null) {
            wheelTimer = new HashedWheelTimer(
                    tickDuration, TimeUnit.MILLISECONDS, ticksPerWheel);
        }
        wheelTimer.start();
        started = true;
    }

    /**
     * stop
     */
    public synchronized void stop() {
        if (wheelTimer != null) {
            wheelTimer.stop();
        }
        started = false;
    }


    /**
     * add task
     */

    public void addTask(String taskId, TimerTask task, long delay, TimeUnit unit) {
        if (wheelTimer != null) {
            Timeout timeout = wheelTimer.newTimeout(task, delay, unit);
            ConcurrentHashMap<String, Timeout> timeoutConcurrentHashMap = timeoutHashMap.get(taskId);
            if (timeoutConcurrentHashMap == null) {
                timeoutConcurrentHashMap = new ConcurrentHashMap<>();
            }
            timeoutConcurrentHashMap.put(taskId, timeout);
            timeoutHashMap.put(taskId, timeoutConcurrentHashMap);
        }
    }

    /**
     * remove task
     */
    public void removeTask(String taskId, TimerTask task) {
        if (!timeoutHashMap.containsKey(taskId)) {
            return;
        }
        ConcurrentHashMap<String, Timeout> timeoutConcurrentHashMap = timeoutHashMap.get(taskId);
        if (timeoutConcurrentHashMap == null || timeoutConcurrentHashMap.isEmpty()) {
            timeoutHashMap.remove(taskId);
            return;
        }

    }
}

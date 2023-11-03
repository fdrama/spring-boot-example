package com.example.springspelexample.support.config;

/**
 * @author fdrama
 * date 2023年07月26日 17:19
 */
public interface LogRecordResolver {
    /**
     * 处理日志
     *
     * @param recordDetail
     */
    void resolveLog(LogRecordDetail recordDetail);
}

package com.example.springspelexample.support.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author fdrama
 * date 2023年08月08日 13:55
 */

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LogRecordDetail {

    /**
     * 日志内容
     */
    private String content;
    /**
     * 保存的操作日志的类型
     */
    private String type;
    /**
     * 日志的子类型
     */
    private String subType;
    /**
     * 日志绑定的业务标识
     */
    private String bizNo;
    /**
     * 额外信息
     */
    private String extra;

    /**
     * 操作人
     */
    private String operatorId;
    /**
     * 操作开始时间
     */
    private long operateTimeBegin;
    /**
     * 操作结束时间
     */
    private long operateTimeEnd;

    /**
     * 操作方法
     */
    private String methodName;

    /**
     * 操作类
     */
    private String className;

    /**
     * 操作方法是否执行成功
     */
    private boolean methodSuccess;

}

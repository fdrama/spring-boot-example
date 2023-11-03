package com.example.springspelexample.service;

import com.example.springspelexample.support.annoation.LogRecord;

/**
 * @author fdrama
 * date 2023年07月27日 13:54
 */
public interface UserService {

    /**
     * 新增用户
     *
     * @param user
     */
    @LogRecord(
            type = LogType.Constants.USER, // 日志类型
            subType = LogSubType.Constants.ADD, // 日志子类型,
            bizNo = "{{#user.userId}}", // 日志业务号
            operatorId = "{{#content}}", // 操作人id
            condition = "{{#user.userId != null}}",// 记录日志条件，默认记录
            successCondition = "{{#_result != null}}", // 记录成功日志条件 否则记录失败记录，默认记录成功日志
            successTemplate = "新增用户 {{#user.userId?:#user.email}} {_SENSITIVE{#user.email,'EMAIL'}}", // 成功日志模板 和失败日志模板不能同时为空
            failTemplate = "新增用户失败 {{#user.userId?:#user.email}}",  // 失败日志模板 和成功日志模板不能同时为空
            extra = "extra info" // 额外信息
    )
    User add(User user);

    /**
     * 更新用户
     *
     * @param user
     */
    @LogRecord(type = LogType.Constants.USER,
            subType = LogSubType.Constants.UPDATE,
            bizNo = "{{#user.userId}}",
            successCondition = "{{#_result != null}}",
            successTemplate = "修改用户 {{#diffContent}} "
    )
    User update(User user);
}

package com.example.springspelexample.service;

import com.example.springspelexample.support.annoation.LogRecord;

/**
 * @author fdrama
 * date 2023年07月27日 13:54
 */
public interface UserService {

    @LogRecord(type = LogType.Constants.PERSON, bizNo = "{{#inventor.id}}", content = "update user")

    @LogRecord(type = LogType.Constants.DEPARTMENT, bizNo = "{{#inventor.id}}", content = "update user")

    public void updateUser(Inventor inventor);
}

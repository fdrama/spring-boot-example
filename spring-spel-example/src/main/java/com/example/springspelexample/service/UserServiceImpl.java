package com.example.springspelexample.service;

import com.example.springspelexample.support.expression.LogRecordThreadContext;
import org.springframework.stereotype.Service;

/**
 * @author fdrama
 * date 2023年07月27日 14:15
 */
@Service
public class UserServiceImpl implements UserService {


    @Override
    public User add(User user) {
        LogRecordThreadContext.putVariable("content", "extra info");
        return user;
    }

    @Override
    public User update(User user) {

        return user;
    }
}

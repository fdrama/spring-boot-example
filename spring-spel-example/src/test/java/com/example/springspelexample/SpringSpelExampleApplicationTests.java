package com.example.springspelexample;

import com.example.springspelexample.service.User;
import com.example.springspelexample.service.UserService;
import com.example.springspelexample.support.expression.LogRecordThreadContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringSpelExampleApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }


    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("dsadsad@163.com");
        user.setUserId("123");
        user.setGender(0);
        LogRecordThreadContext.putVariable("content", "123");
        userService.add(user);
    }


}

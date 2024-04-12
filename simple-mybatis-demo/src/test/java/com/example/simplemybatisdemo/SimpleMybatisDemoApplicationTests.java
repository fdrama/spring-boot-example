package com.example.simplemybatisdemo;

import com.example.simplemybatisdemo.dao.entity.User;
import com.example.simplemybatisdemo.dao.enums.Gender;
import com.example.simplemybatisdemo.dao.mapper.UserMapper;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
class SimpleMybatisDemoApplicationTests {

    @Autowired
    private UserMapper userMapper;


    @Test
    public void testInt(){
        Integer i = 2;
        Integer j = 2;
        Assert.isTrue(i == j, "i != j");
    }

    @Test
    void contextLoads() {
        List<User> users = userMapper.selectAll();
        Assert.isTrue(users.size() > 0, "查询失败");
    }

    @Test
    void testInsert(){
        User user = new User();
        user.setUsername("test1123");
        user.setEmail("213311");
        user.setPassword("123456");
        user.setFirstName("test");
        user.setLastName("test");
        user.setPhone("12345678901");
        user.setGender(Gender.MALE);
        user.setAvatar("");
        userMapper.insert(user);
    }

}

package com.example.db;

import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author fdrama
 * date 2023年05月08日 14:27
 */


@SpringBootTest
@Slf4j
public class UserServiceTest {

    private UserService userService;

    @Test
    public void test() {
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}

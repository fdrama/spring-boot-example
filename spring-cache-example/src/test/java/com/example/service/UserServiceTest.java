package com.example.service;

import com.example.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author fdrama
 * date 2023年05月12日 15:33
 */
@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;


    @Test
    public void test() {
        log.info("find user id : {}, result : {}", 1, userService.findUser(1));
        log.info("find user id : {}, result : {}", 1, userService.findUser(1));
        log.info("find user id : {}, result : {}", 1, userService.findUser(1));
        log.info("update user id {}, result : {}", 1, userService.updateUser(new User(1, "斯派克")));
        log.info("find user id : {}, result : {}", 1, userService.findUser(1));
        log.info("remove user id : {}, result : {}", 1, userService.removeUser(1));
        log.info("find user id : {}, result : {}", 1, userService.findUser(1));
        log.info("find user id : {}, result : {}", 1, userService.findUser(2));
        log.info("find user id : {}, result : {}", 1, userService.findUser(2));
        log.info("load all user");
        userService.loadAll();
        log.info("find user id : {}, result : {}", 1, userService.findUser(1));
        log.info("find user id : {}, result : {}", 1, userService.findUser(2));
    }
}

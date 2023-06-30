package com.example.service;

import com.example.repository.UserRepository;
import com.example.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author fdrama
 * date 2023年05月12日 15:33
 */
@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() {

        List<User> users = userRepository.findAll();
        log.info("users:{}", users);
    }
}

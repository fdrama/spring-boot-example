package com.example.db;

import com.example.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApplicationTests {

  @Test
  void contextLoads() {
  }

  @Resource
  private UserMapper userMapper;




}

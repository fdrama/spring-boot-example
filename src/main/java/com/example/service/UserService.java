package com.example.service;

import com.example.mapper.UserMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fdrama
 * date 2023年05月08日 14:05
 */
@Service
@CacheConfig(cacheNames = "cache_user", cacheManager = "redisCacheManager")
public class UserService {

    @Resource
    private UserMapper userMapper;


}

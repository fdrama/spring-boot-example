package com.example.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;

import java.lang.reflect.Method;

/**
 * @author fdrama
 * date 2023年05月10日 15:43
 */
public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return method.getDeclaringClass().getSimpleName().concat(new SimpleKey(params).toString());
    }
}

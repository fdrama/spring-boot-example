package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author fdrama
 * date 2023年05月10日 10:27
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public KeyGenerator keyGenerator(){
        return new CustomKeyGenerator();
    }


    @Bean("caffeineCacheManager")
    @Primary
    public CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }


    @Bean("redisCacheManager")
    public CacheManager redisCacheManager(@Autowired RedisTemplate<Object, Object> template) {

        RedisConnectionFactory connectionFactory = template.getConnectionFactory();

        if (connectionFactory == null) {
            throw new IllegalStateException("RedisConnectionFactory is required");
        }

        return RedisCacheManager.RedisCacheManagerBuilder
                // Redis 连接工厂
                .fromConnectionFactory(template.getConnectionFactory())
                // 缓存配置
                .cacheDefaults(getCacheConfigurationWithTtl(60 * 60))
                .withCacheConfiguration("cache_user", getCacheConfigurationWithTtl(60))
                // 配置同步修改或删除 put/evict
                .transactionAware()
                .build();
    }

    RedisCacheConfiguration getCacheConfigurationWithTtl(long seconds) {

        return RedisCacheConfiguration
                .defaultCacheConfig()
                // 设置key为String
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer.UTF_8))
                // 设置value 为自动转Json的Object
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 缓存数据保存1小时
                .entryTtl(Duration.ofSeconds(seconds));
    }
}

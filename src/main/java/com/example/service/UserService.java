package com.example.service;

import com.example.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author fdrama
 * date 2023年05月08日 14:05
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "users")
public class UserService {

    private static final List<User> USER_LIST = new ArrayList<>();

    static {
        USER_LIST.add(new User(1, "汤姆"));
        USER_LIST.add(new User(2, "杰瑞"));
        USER_LIST.add(new User(3, "斯派克"));
    }

    @Cacheable()
    public User findUser(int id) {
        slowService();
        return USER_LIST.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    @Cacheable(key = "#user.id")
    public User findUser(User user) {
        slowService();


        return Stream.of(
                        USER_LIST.stream().filter(u -> u.getId().equals(user.getId())).findFirst().orElse(null),
                        USER_LIST.stream().filter(u -> u.getName().equals(user.getName())).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        return user;
    }

    @CacheEvict(key = "#id")
    public boolean removeUser(int id){
        // no thing
        return true;
    }



    @CacheEvict(allEntries = true)
    public void loadAll() {
    }

    private void slowService() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

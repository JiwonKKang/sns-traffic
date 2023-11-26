package com.fast.campus.simplesns.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, User> userRedisTemplate;

    public void setUser(User user) {
        String key = getKey(user.getName());
        log.info("Set User from {} : {}", key, user);
        userRedisTemplate.opsForValue().set(key, user);
    }

    public User getUser(String userName) {
        String key = getKey(userName);
        User user = userRedisTemplate.opsForValue().get(key);
        log.info("Get User from {} : {}", key, user);
        return user;
    }

    public String getKey(String userName) {
        return "USER:" + userName;
    }

}

package io.github.pedrozaz.chatserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPresenceService {

    private final static String ONLINE_USERS_KEY = "chat:online_users";
    private final StringRedisTemplate redisTemplate;

    public void connectUser(String username) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
        log.info("Connected user {}", username);
    }

    public void disconnectUser(String username) {
        if (username != null) {
            redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
            log.info("Disconnected user {}", username);
        }
    }

    public Set<String> getOnlineUsers() {
        return redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }

    public Long countOnlineUsers() {
        return redisTemplate.opsForSet().size(ONLINE_USERS_KEY);
    }
}

package io.github.pedrozaz.chatserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pedrozaz.chatserver.config.RedisConfig;
import io.github.pedrozaz.chatserver.dto.ChatMessageDTO;
import io.github.pedrozaz.chatserver.service.ChatService;
import io.github.pedrozaz.chatserver.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final UserPresenceService presenceService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) throws JsonProcessingException {
        chatService.save(chatMessageDTO);
        broadcast(chatMessageDTO);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDTO chatMessageDTO,
                                  SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessageDTO.sender());
        presenceService.connectUser(chatMessageDTO.sender());
        chatService.save(chatMessageDTO);
        broadcast(chatMessageDTO);
    }

    private void broadcast(ChatMessageDTO chatMessageDTO) throws  JsonProcessingException {
        String json = objectMapper.writeValueAsString(chatMessageDTO);
        redisTemplate.convertAndSend(RedisConfig.CHAT_TOPIC, json);
    }
}

package io.github.pedrozaz.chatserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pedrozaz.chatserver.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    /*
    Method invoked by RedisMessageListenerContainer when new message comes
     */
    public void onMessage(String messageJson) {
        try {
            ChatMessageDTO chatMessage = objectMapper.readValue(messageJson, ChatMessageDTO.class);
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
            log.debug("Redis -> WebSocket: {}", chatMessage);
        } catch (Exception e) {
            log.error("Error while processing Redis message", e);
        }
    }
}

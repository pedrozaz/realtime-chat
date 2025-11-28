package io.github.pedrozaz.chatserver.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pedrozaz.chatserver.config.RedisConfig;
import io.github.pedrozaz.chatserver.domain.ChatMessage;
import io.github.pedrozaz.chatserver.dto.ChatMessageDTO;
import io.github.pedrozaz.chatserver.service.ChatService;
import io.github.pedrozaz.chatserver.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserPresenceService presenceService;
    private final StringRedisTemplate redisTemplate;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.debug("New socket connection received.");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("username");

        if(username != null) {
            presenceService.disconnectUser(username);

            var chatMessage = new ChatMessageDTO(
                    ChatMessage.MessageType.LEAVE,
                    username + " left",
                    username
            );

            try {
                String json = objectMapper.writeValueAsString(chatMessage);
                redisTemplate.convertAndSend(RedisConfig.CHAT_TOPIC, json);
            } catch (JsonProcessingException e) {
                log.error("Error while serializing leaving event", e);
            }
        }
    }
}

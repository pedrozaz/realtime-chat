package io.github.pedrozaz.chatserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pedrozaz.chatserver.config.RedisConfig;
import io.github.pedrozaz.chatserver.domain.ChatMessage;
import io.github.pedrozaz.chatserver.dto.ChatMessageDTO;
import io.github.pedrozaz.chatserver.service.ChatService;
import io.github.pedrozaz.chatserver.service.UserPresenceService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final UserPresenceService presenceService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) throws JsonProcessingException {
        chatService.save(chatMessageDTO);
        broadcast(chatMessageDTO);
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessageDTO chatMessageDTO) throws JsonProcessingException {
        if (chatMessageDTO.recipient() == null || chatMessageDTO.recipient().isEmpty()) {
            throw new IllegalArgumentException("Recipient required for private message");
        }

        chatService.save(chatMessageDTO);
        broadcast(chatMessageDTO);
    }


    @MessageMapping("/chat.getOnlineUsers")
    public void getOnlineUsers(Principal principal) {
        if (principal == null) return;

        Set<String> onlineUsers = presenceService.getOnlineUsers();
        simpMessagingTemplate.convertAndSendToUser(
                principal.getName(),
                "queue/online-users",
                onlineUsers
        );
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDTO chatMessageDTO,
                                  SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessageDTO.sender());
        presenceService.connectUser(chatMessageDTO.sender());
        chatService.save(chatMessageDTO);
        broadcast(chatMessageDTO);
    }


    @GetMapping("/messages")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> findChatMessages(@RequestParam(required = false) String recipient,
                                                              Principal principal) {
        return ResponseEntity.ok(chatService.findChatMessages(principal.getName(), recipient));
    }

    private void broadcast(ChatMessageDTO chatMessageDTO) throws  JsonProcessingException {
        String json = objectMapper.writeValueAsString(chatMessageDTO);
        redisTemplate.convertAndSend(RedisConfig.CHAT_TOPIC, json);
    }
}

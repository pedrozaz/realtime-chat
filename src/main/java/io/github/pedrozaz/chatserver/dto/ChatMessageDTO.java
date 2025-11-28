package io.github.pedrozaz.chatserver.dto;

import io.github.pedrozaz.chatserver.domain.ChatMessage;

public record ChatMessageDTO(
        ChatMessage.MessageType type,
        String content,
        String sender
) {
}

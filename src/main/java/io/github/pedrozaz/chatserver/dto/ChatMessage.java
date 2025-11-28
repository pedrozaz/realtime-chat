package io.github.pedrozaz.chatserver.dto;

public record ChatMessage (
        MessageType type,
        String content,
        String sender
) {
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}

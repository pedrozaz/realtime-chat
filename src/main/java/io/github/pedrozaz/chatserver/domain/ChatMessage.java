package io.github.pedrozaz.chatserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;

    private String content;
    private String sender;
    private String recipient;
    private MessageType type;

    @Builder.Default
    private Instant timestamp = Instant.now();

    public enum MessageType {
        JOIN, LEAVE, CHAT
    }
}

package io.github.pedrozaz.chatserver.domain;

import io.github.pedrozaz.chatserver.dto.ChatMessageDTO;
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
    private ChatMessageDTO.MessageType type;

    @Builder.Default
    private Instant timestamp = Instant.now();
}

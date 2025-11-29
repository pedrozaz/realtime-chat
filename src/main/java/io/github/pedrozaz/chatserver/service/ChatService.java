package io.github.pedrozaz.chatserver.service;

import io.github.pedrozaz.chatserver.domain.ChatMessage;
import io.github.pedrozaz.chatserver.dto.ChatMessageDTO;
import io.github.pedrozaz.chatserver.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository repository;

    public void save(ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = ChatMessage.builder()
                .content(chatMessageDTO.content())
                .sender(chatMessageDTO.sender())
                .recipient(chatMessageDTO.recipient())
                .type(ChatMessage.MessageType.valueOf(chatMessageDTO.type().name()))
                .build();

        repository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(String sender, String recipient) {
        if (recipient == null || recipient.isEmpty()) {
            return repository.findByRecipientIsNullOrderByTimestampAsc();
        }
        return repository.findPrivateMessages(sender, recipient);
    }
}

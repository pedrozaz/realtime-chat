package io.github.pedrozaz.chatserver.repository;

import io.github.pedrozaz.chatserver.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRecipientIsNullOrderByTimestampAsc();

    @Query(value = "{ '$or':  [ { 'sender':  ?0, 'recipient': ?1 }, { 'sender': ?1, 'recipient': ?0 } ] }", sort = "{ 'timestamp': 1 }")
    List<ChatMessage> findPrivateMessages(String user1, String user2);
}


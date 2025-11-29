package io.github.pedrozaz.chatserver.repository;

import io.github.pedrozaz.chatserver.domain.User;

import java.util.Optional;

public interface MongoRepository extends org.springframework.data.mongodb.repository.MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}

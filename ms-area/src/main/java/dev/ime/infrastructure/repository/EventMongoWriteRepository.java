package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import dev.ime.infrastructure.entity.EventMongoEntity;

public interface EventMongoWriteRepository extends ReactiveMongoRepository<EventMongoEntity, UUID>{

}

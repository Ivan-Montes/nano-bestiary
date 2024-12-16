package dev.ime.infrastructure.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreatureRedisEntity {

	@Id
	private UUID creatureId;

	private UUID areaId;
	
}

package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import dev.ime.infrastructure.entity.AreaRedisEntity;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @Mock
    private ReactiveRedisConnectionFactory redisFactory;

    @InjectMocks
    private RedisConfig redisConfig;

	@Test
	void reactiveRedisTemplate_ShouldCreateCorrectTemplate() {
		
        ReactiveRedisTemplate<String, AreaRedisEntity> template = redisConfig.reactiveRedisTemplate(redisFactory);

        org.junit.jupiter.api.Assertions.assertAll(
        		()-> Assertions.assertThat(template).isNotNull()
        		);

	}

	@Test
	void stringReactiveRedisTemplate_ShouldCreateCorrectTemplate() {
		
        ReactiveRedisTemplate<String, String> template = redisConfig.stringReactiveRedisTemplate(redisFactory);

        org.junit.jupiter.api.Assertions.assertAll(
        		()-> Assertions.assertThat(template).isNotNull()
        		);

	}

}

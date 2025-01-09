package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import dev.ime.infrastructure.entity.AreaRedisEntity;

@Configuration
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate<String, AreaRedisEntity> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<AreaRedisEntity> serializer = new Jackson2JsonRedisSerializer<>(AreaRedisEntity.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, AreaRedisEntity> builder =
            RedisSerializationContext.newSerializationContext( RedisSerializer.string() );
        
        RedisSerializationContext<String, AreaRedisEntity> context = builder
            .value(serializer)
            .build();
        
        return new ReactiveRedisTemplate<>(factory, context);
        
    }

    @Bean
    ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext.RedisSerializationContextBuilder<String, String> builder =
            RedisSerializationContext.newSerializationContext(RedisSerializer.string());

        RedisSerializationContext<String, String> context = builder
            .value(RedisSerializer.string())
            .build();

        return new ReactiveRedisTemplate<>(factory, context);
        
    }

}

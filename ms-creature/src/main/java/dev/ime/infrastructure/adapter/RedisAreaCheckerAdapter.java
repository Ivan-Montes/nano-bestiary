package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.application.utils.ResilienceConfigUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.ports.outbound.EntityCheckerPort;
import dev.ime.infrastructure.entity.AreaRedisEntity;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;

@Component
@Qualifier("redisAreaCheckerAdapter")
public class RedisAreaCheckerAdapter implements EntityCheckerPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
    private final ReactiveRedisTemplate<String, AreaRedisEntity> reactiveRedisTemplate;
	private final ResilienceConfigUtils resilienceConfigUtils;

	public RedisAreaCheckerAdapter(ReactiveLoggerUtils reactiveLoggerUtils,
			ReactiveRedisTemplate<String, AreaRedisEntity> reactiveRedisTemplate, ResilienceConfigUtils resilienceConfigUtils) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.resilienceConfigUtils = resilienceConfigUtils;
	}

	@Override
	public Mono<Boolean> existsById(UUID areaId) {
		
		return Mono.just(areaId)
				.flatMap( id -> reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_PROCESS, id.toString()).thenReturn(id))
		        .flatMap( id -> reactiveRedisTemplate.hasKey( GlobalConstants.AREA_CAT  + ":" + id.toString() ))
		        .transformDeferred(CircuitBreakerOperator.of(resilienceConfigUtils.getCircuitBreaker()));

	}

}

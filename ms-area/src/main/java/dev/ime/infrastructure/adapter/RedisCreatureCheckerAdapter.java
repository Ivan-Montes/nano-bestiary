package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.application.utils.ResilienceConfigUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.ports.outbound.CreatureEntityCheckerPort;
import dev.ime.domain.ports.outbound.EntityCheckerPort;
import dev.ime.infrastructure.entity.CreatureRedisEntity;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;

@Component
@Qualifier("redisCreatureCheckerAdapter")
public class RedisCreatureCheckerAdapter implements EntityCheckerPort, CreatureEntityCheckerPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
    private final ReactiveRedisTemplate<String, CreatureRedisEntity> reactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate;
	private final ResilienceConfigUtils resilienceConfigUtils;

	public RedisCreatureCheckerAdapter(ReactiveLoggerUtils reactiveLoggerUtils,
			ReactiveRedisTemplate<String, CreatureRedisEntity> reactiveRedisTemplate,
			ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate, ResilienceConfigUtils resilienceConfigUtils) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.reactiveRedisTemplate = reactiveRedisTemplate;
		this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
        this.resilienceConfigUtils = resilienceConfigUtils;
	}

	@Override
	public Mono<Boolean> existsById(UUID creatureId) {
		
		return Mono.just(creatureId)
				.flatMap( id -> reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_PROCESS, id.toString()).thenReturn(id))
				.flatMap( id -> reactiveRedisTemplate.hasKey( GlobalConstants.CREATURE_CAT  + ":" + id.toString()))
		        .transformDeferred(CircuitBreakerOperator.of(resilienceConfigUtils.getCircuitBreaker()));
		
	}

	@Override
	public Mono<Boolean> existsAnyByAreaId(UUID areaId) {

		return Mono.justOrEmpty(areaId)
				.flatMap( id -> reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_PROCESS, id.toString()).thenReturn(id))
				.switchIfEmpty( Mono.error(new IllegalArgumentException(GlobalConstants.AREA_ID)))
				.map( id -> GlobalConstants.AREA_CAT_INDEX + id)
				.flatMap( indexKey -> stringReactiveRedisTemplate
								.opsForSet()
								.size(indexKey)
								.map(size -> size > 0)
								)
		        .transformDeferred(CircuitBreakerOperator.of(resilienceConfigUtils.getCircuitBreaker()));
	    
	}
	
}

package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;

import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.application.utils.ResilienceConfigUtils;
import dev.ime.config.GlobalConstants;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RedisCreatureCheckerAdapterTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
	@Mock
	private ResilienceConfigUtils resilienceConfigUtils;

	@InjectMocks
	private RedisCreatureCheckerAdapter redisCreatureCheckerAdapter;
	
	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();	
	
	@Test
	void existsById_shouldReturnTrue() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(reactiveRedisTemplate.hasKey(Mockito.anyString())).thenReturn(Mono.just(true));
		Mockito.when(resilienceConfigUtils.getCircuitBreaker()).thenReturn(CircuitBreaker.ofDefaults(GlobalConstants.MSG_PATTERN_SEVERE));

		StepVerifier
		.create(redisCreatureCheckerAdapter.existsById(creatureId01))
		.expectNext(true)
        .verifyComplete();

		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(reactiveRedisTemplate).hasKey(Mockito.anyString());
		Mockito.verify(resilienceConfigUtils).getCircuitBreaker();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void existsAnyByAreaId_shouldReturnTrue() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.size(Mockito.anyString())).thenReturn(Mono.just(1L));
		Mockito.when(resilienceConfigUtils.getCircuitBreaker()).thenReturn(CircuitBreaker.ofDefaults(GlobalConstants.MSG_PATTERN_SEVERE));
		
		StepVerifier
		.create(redisCreatureCheckerAdapter.existsAnyByAreaId(areaId01))
		.expectNext(true)
        .verifyComplete();

		Mockito.verify(reactiveRedisTemplate).opsForSet();
		Mockito.verify(reactiveSetOperations).size(Mockito.anyString());
		Mockito.verify(resilienceConfigUtils).getCircuitBreaker();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void existsAnyByAreaId_shouldReturnFalse() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.size(Mockito.anyString())).thenReturn(Mono.just(0L));
		Mockito.when(resilienceConfigUtils.getCircuitBreaker()).thenReturn(CircuitBreaker.ofDefaults(GlobalConstants.MSG_PATTERN_SEVERE));
		
		StepVerifier
		.create(redisCreatureCheckerAdapter.existsAnyByAreaId(areaId01))
		.expectNext(false)
        .verifyComplete();

		Mockito.verify(reactiveRedisTemplate).opsForSet();
		Mockito.verify(reactiveSetOperations).size(Mockito.anyString());
		Mockito.verify(resilienceConfigUtils).getCircuitBreaker();
		
	}
	
}

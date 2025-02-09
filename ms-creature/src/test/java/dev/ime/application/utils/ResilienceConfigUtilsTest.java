package dev.ime.application.utils;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

@ExtendWith(MockitoExtension.class)
class ResilienceConfigUtilsTest {

	@Mock
	private CircuitBreaker circuitBreaker;

	@Mock
	private Bulkhead bulkhead;

	@InjectMocks
	private ResilienceConfigUtils resilienceConfigUtils;
	
	@Test
	void getCircuitBreaker_shouldReturnBean() {
		
		CircuitBreaker circuitBreakerBean = resilienceConfigUtils.getCircuitBreaker();

		org.junit.jupiter.api.Assertions.assertAll(
        		()-> Assertions.assertThat(circuitBreakerBean).isNotNull()
        		);
		
	}

	@Test
	void getBulkhead_shouldReturnBean() {

		Bulkhead bulkheadBean = resilienceConfigUtils.getBulkhead();

        org.junit.jupiter.api.Assertions.assertAll(
        		()-> Assertions.assertThat(bulkheadBean).isNotNull()
        		);

	}

}

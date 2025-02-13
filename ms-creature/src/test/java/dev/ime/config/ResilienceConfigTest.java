package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

@ExtendWith(MockitoExtension.class)
class ResilienceConfigTest {

    @InjectMocks
    private ResilienceConfig resilienceConfig;
    
	@Test
	void circuitBreakerBean_shouldReturnBean() {
		
		CircuitBreaker circuitBreakerBean = resilienceConfig.circuitBreakerBean();

        org.junit.jupiter.api.Assertions.assertAll(
        		()-> Assertions.assertThat(circuitBreakerBean).isNotNull()
        		);

	}

	@Test
	void bulkheadBean_shouldReturnBean() {
		
		Bulkhead bulkheadBean = resilienceConfig.bulkheadBean();

        org.junit.jupiter.api.Assertions.assertAll(
        		()-> Assertions.assertThat(bulkheadBean).isNotNull()
        		);

	}

}

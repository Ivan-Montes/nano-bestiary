package dev.ime.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

@Configuration
public class ResilienceConfig {
	
	@Bean
	CircuitBreaker circuitBreakerBean() {
		
		CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        		.failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(4)
                .build();

        return CircuitBreaker.of("rSocketCheckerCircuitBreaker", config);
    }

    @Bean
    Bulkhead bulkheadBean() {
    	
        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(10)
            .maxWaitDuration(Duration.ofMillis(500))
            .build();
        
        return Bulkhead.of("rSocketCheckerBulkhead", config);
    }
	
}

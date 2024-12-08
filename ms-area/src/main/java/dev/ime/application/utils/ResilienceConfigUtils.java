package dev.ime.application.utils;

import org.springframework.stereotype.Component;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

@Component
public class ResilienceConfigUtils {
	
	private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;

    public ResilienceConfigUtils(CircuitBreaker circuitBreaker, Bulkhead bulkhead) {
        this.circuitBreaker = circuitBreaker;
        this.bulkhead = bulkhead;
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public Bulkhead getBulkhead() {
        return bulkhead;
    }
}

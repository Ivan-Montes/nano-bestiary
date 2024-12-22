package dev.ime.infrastructure.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketRequester.Builder;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.application.utils.ResilienceConfigUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.ports.outbound.CreatureEntityCheckerPort;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@Qualifier("rSocketCreatureCheckerAdapter")
public class RSocketCreatureCheckerAdapter  implements CreatureEntityCheckerPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
	private final RSocketRequester.Builder rsocketRequesterBuilder;	
	private final EurekaClient eurekaClient;
	private final ResilienceConfigUtils resilienceConfigUtils;

    public RSocketCreatureCheckerAdapter(ReactiveLoggerUtils reactiveLoggerUtils, Builder rsocketRequesterBuilder,
                                      EurekaClient eurekaClient, ResilienceConfigUtils resilienceConfigUtils) {
        this.reactiveLoggerUtils = reactiveLoggerUtils;
        this.rsocketRequesterBuilder = rsocketRequesterBuilder;
        this.eurekaClient = eurekaClient;
        this.resilienceConfigUtils = resilienceConfigUtils;
    }

	@Override
	public Mono<Boolean> existsAnyByAreaId(UUID areaId) {
	    
		return Mono.just(areaId)
	        .flatMap(id -> reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_PROCESS, id.toString()).thenReturn(id))
	        .flatMap(this::createConnectionInfo)
	        .flatMap(this::consultExternalMicroserviceWithRSocket)
            .transformDeferred(BulkheadOperator.of(resilienceConfigUtils.getBulkhead()))
            .transformDeferred(CircuitBreakerOperator.of(resilienceConfigUtils.getCircuitBreaker()));
		
	}

	private Mono<Map<String, String>> createConnectionInfo(UUID id){
		
		return Mono.fromCallable(()->{
			
			InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(GlobalConstants.MS_CREATURE, false);
		    String host = instanceInfo.getIPAddr();
		    String rsocketPort = instanceInfo.getMetadata().get(GlobalConstants.RSOCKET_PORT);
			Map<String, String> resultMap = new HashMap<>();
			resultMap.put(GlobalConstants.HOST, host);
			resultMap.put(GlobalConstants.RSOCKET_PORT, rsocketPort);
			resultMap.put(GlobalConstants.AREA_ID, id.toString());
			
			return resultMap;
			
    	}).onErrorMap( e -> new IllegalArgumentException(GlobalConstants.EX_EVENT_UNEXPEC, e));
		
	}

	private Mono<Boolean> consultExternalMicroserviceWithRSocket(Map<String, String> map) {
		
		return rsocketRequesterBuilder
		        .tcp(map.get(GlobalConstants.HOST), Integer.parseInt(map.get(GlobalConstants.RSOCKET_PORT)))
		        .route("creatures.existsAnyByAreaId." + map.get(GlobalConstants.AREA_ID))
		        .retrieveMono(Boolean.class)
		        .retryWhen(Retry.backoff(GlobalConstants.RSOCKET_MAX_RETRIES, GlobalConstants.RSOCKET_RETRIES_DELAY))
		        .timeout(GlobalConstants.RSOCKET_TIMEOUT);
		
	}
	
}

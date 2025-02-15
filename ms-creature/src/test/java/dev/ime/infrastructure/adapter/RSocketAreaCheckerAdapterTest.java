package dev.ime.infrastructure.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketRequester.RequestSpec;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.application.utils.ResilienceConfigUtils;
import dev.ime.config.GlobalConstants;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RSocketAreaCheckerAdapterTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
	private RSocketRequester.Builder rsocketRequesterBuilder;
	@Mock	
	private EurekaClient eurekaClient;
	@Mock
	private ResilienceConfigUtils resilienceConfigUtils;

	@InjectMocks
	private RSocketAreaCheckerAdapter rSocketAreaCheckerAdapter;

	private final UUID areaId01 = UUID.randomUUID();	
	private final String host = "127.0.0.1";
	private final String rsocketPort = "44444";
	private Map<String, String> metadataMap;

	@BeforeEach
	private void setUp() {	
		
		metadataMap = new HashMap<>();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void existsById_shouldReturnTrue() {

		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		InstanceInfo instanceInfo = Mockito.mock(InstanceInfo.class);
		Mockito.when(eurekaClient.getNextServerFromEureka(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(instanceInfo);
		Mockito.when(instanceInfo.getIPAddr()).thenReturn(host);
		metadataMap.put(GlobalConstants.RSOCKET_PORT, rsocketPort);
		Mockito.when(instanceInfo.getMetadata()).thenReturn(metadataMap);
		RSocketRequester rSocketRequester = Mockito.mock(RSocketRequester.class);
		Mockito.when(rsocketRequesterBuilder.tcp(Mockito.anyString(), Mockito.anyInt())).thenReturn(rSocketRequester);
		RequestSpec requestSpec = Mockito.mock(RequestSpec.class);
		Mockito.when(rSocketRequester.route(Mockito.anyString())).thenReturn(requestSpec);
		Mockito.when(requestSpec.retrieveMono(Mockito.any(Class.class))).thenReturn(Mono.just(true));
		Mockito.when(resilienceConfigUtils.getBulkhead()).thenReturn(Bulkhead.ofDefaults(GlobalConstants.MSG_PATTERN_SEVERE));
		Mockito.when(resilienceConfigUtils.getCircuitBreaker()).thenReturn(CircuitBreaker.ofDefaults(GlobalConstants.MSG_PATTERN_SEVERE));
		
		StepVerifier
		.create(rSocketAreaCheckerAdapter.existsById(areaId01))
		.expectNext(true)
		.verifyComplete();

		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(eurekaClient).getNextServerFromEureka(Mockito.anyString(), Mockito.anyBoolean());
		Mockito.verify(instanceInfo).getIPAddr();
		Mockito.verify(instanceInfo).getMetadata();
		Mockito.verify(rsocketRequesterBuilder).tcp(Mockito.anyString(), Mockito.anyInt());
		Mockito.verify(rSocketRequester).route(Mockito.anyString());
		Mockito.verify(requestSpec).retrieveMono(Mockito.any(Class.class));
		Mockito.verify(resilienceConfigUtils).getBulkhead();
		Mockito.verify(resilienceConfigUtils).getCircuitBreaker();
		
	}

	@Test
	void existsAnyByAreaId_WithGenerateException_shouldReturnError() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(resilienceConfigUtils.getBulkhead()).thenReturn(Bulkhead.ofDefaults(GlobalConstants.MSG_PATTERN_SEVERE));
		Mockito.when(resilienceConfigUtils.getCircuitBreaker()).thenReturn(CircuitBreaker.ofDefaults(GlobalConstants.MSG_PATTERN_SEVERE));
		
		StepVerifier
		.create(rSocketAreaCheckerAdapter.existsById(areaId01))
		.expectError(IllegalArgumentException.class)
		.verify();
		
		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(resilienceConfigUtils).getBulkhead();
		Mockito.verify(resilienceConfigUtils).getCircuitBreaker();
	}
	
}

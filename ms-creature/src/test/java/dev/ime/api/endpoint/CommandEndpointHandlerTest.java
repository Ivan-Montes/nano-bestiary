package dev.ime.api.endpoint;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.CreatureDto;
import dev.ime.application.dto.ErrorResponse;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.config.UriConfigProperties;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.inbound.CommandServicePort;
import reactor.core.publisher.Mono;

@WebFluxTest({CommandEndpointHandler.class, CommandEndpointRouter.class})
class CommandEndpointHandlerTest {

	@MockitoBean
	private CommandServicePort<CreatureDto> commandService;
	@MockitoBean
	private DtoValidator dtoValidator;
	@MockitoBean
	private ErrorHandler errorHandler;
	@MockitoBean
	private ReactiveLoggerUtils reactiveLoggerUtils;

    @Autowired
    private WebTestClient webTestClient;    

	@TestConfiguration
	static class TestConfig {	
		
	    @Bean
	    UriConfigProperties uriConfigProperties() {
	        UriConfigProperties props = new UriConfigProperties();
	        props.setEndpointUri(PATH);
	        return props;
	    }
	    
	    @Bean
	    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	        return http	
	                   .authorizeExchange(exchanges -> exchanges
	                           .anyExchange().permitAll()
	                		   )
		        		.csrf(CsrfSpec::disable)	
	                   .build();
	    }
	}

    private static final String PATH = "/api/v1/creatures";

	private Event event;
	private CreatureDto creatureDto01;

	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureDescription01 = "";

	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREATURE_CAT;
	private final String eventType = GlobalConstants.CREATURE_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();
	
	@BeforeEach
	private void setUp() {	
		
		creatureDto01 = new CreatureDto(creatureId01, creatureName01, creatureDescription01, areaId01);

		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
	}

	@Test
	void create_WithValidEntity_ReturnsCreatedEventWithOkStatus() {
		
		Mockito.when(dtoValidator.validateDto(Mockito.any(CreatureDto.class))).thenReturn(Mono.just(creatureDto01));
		Mockito.when(commandService.create(Mockito.any(CreatureDto.class))).thenReturn(Mono.just(event));
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		
		webTestClient
        .post().uri(PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(creatureDto01), CreatureDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Event.class)
        .value(result -> {
        	org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(result).isNotNull(),
					() -> Assertions.assertThat(result.getEventId()).isEqualTo(event.getEventId()),
					() -> Assertions.assertThat(result.getEventCategory()).isEqualTo(event.getEventCategory())
					);
        	});        
		Mockito.verify(dtoValidator).validateDto(Mockito.any(CreatureDto.class));
		Mockito.verify(commandService).create(Mockito.any(CreatureDto.class));
		
	}

	@Test
	void update_WithValidEntity_ReturnsUpdatedEventWithOkStatus() {

		Mockito.when(dtoValidator.validateDto(Mockito.any(CreatureDto.class))).thenReturn(Mono.just(creatureDto01));
		Mockito.when(commandService.update(Mockito.any(UUID.class),Mockito.any(CreatureDto.class))).thenReturn(Mono.just(event));
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		
		webTestClient
        .put().uri(PATH + "/{id}", creatureId01.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(creatureDto01), CreatureDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Event.class)
        .value(result -> {
        	org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(result).isNotNull(),
					() -> Assertions.assertThat(result.getEventId()).isEqualTo(event.getEventId()),
					() -> Assertions.assertThat(result.getEventCategory()).isEqualTo(event.getEventCategory())
					);
        	});		
		Mockito.verify(dtoValidator).validateDto(Mockito.any(CreatureDto.class));
		Mockito.verify(commandService).update(Mockito.any(UUID.class),Mockito.any(CreatureDto.class));
		
	}

	@Test
	void update_WithInvalidId_ReturnsServerResponseError() {

		Mono<ServerResponse> serverResponse = ServerResponse
                .status(HttpStatus.I_AM_A_TEAPOT)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createErrorResponse());
                
		Mockito.when(errorHandler.handleException(Mockito.any(Throwable.class))).thenReturn(serverResponse);
		
		webTestClient
		.put().uri(PATH + "/{id}", Collections.singletonMap("id", "id-bad-format"))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(creatureDto01), CreatureDto.class)
        .exchange()
        .expectStatus().is4xxClientError()
		.expectBody(ErrorResponse.class)
		.value( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.name()).isEqualTo(GlobalConstants.EX_ILLEGALARGUMENT)
					);
			});
        
	}

	private ErrorResponse createErrorResponse() {
		
		return new ErrorResponse(
        				UUID.randomUUID(),
        				GlobalConstants.EX_ILLEGALARGUMENT,
        				GlobalConstants.EX_ILLEGALARGUMENT_DESC,
        				Map.of("","")
            		);
	}

	@Test
	void deleteById_WithValidId_ReturnsDeletedEventWithOkStatus() {

		Mockito.when(commandService.deleteById(Mockito.any(UUID.class))).thenReturn(Mono.just(event));
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		
		webTestClient
		.delete().uri(PATH + "/{id}", Collections.singletonMap("id", creatureId01))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Event.class)
        .value(result -> {
        	org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(result).isNotNull(),
					() -> Assertions.assertThat(result.getEventId()).isEqualTo(event.getEventId()),
					() -> Assertions.assertThat(result.getEventCategory()).isEqualTo(event.getEventCategory())
					);
        	});	
		Mockito.verify(commandService).deleteById(Mockito.any(UUID.class));
		
	}

}

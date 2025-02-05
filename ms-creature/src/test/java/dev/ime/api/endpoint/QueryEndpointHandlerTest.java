package dev.ime.api.endpoint;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
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
import dev.ime.application.dto.PaginationDto;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.application.utils.SortingValidationUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.config.UriConfigProperties;
import dev.ime.domain.ports.inbound.QueryServicePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest({QueryEndpointHandler.class, QueryEndpointRouter.class})
class QueryEndpointHandlerTest {

	@MockitoBean
	private QueryServicePort<CreatureDto> queryService;
	@MockitoBean
	private DtoValidator dtoValidator;
	@MockitoBean
	private SortingValidationUtils sortingValidator;
	@MockitoBean
	private ErrorHandler errorHandler;

    @Autowired
    private WebTestClient webTestClient;    

    @MockitoBean
	private ReactiveLoggerUtils reactiveLoggerUtils;

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

	private CreatureDto creatureDto01;
	private CreatureDto creatureDto02;

	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID creatureId02 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureName02 = "";
	private final String creatureDescription01 = "";
	private final String creatureDescription02 = "";
	
	@BeforeEach
	private void setUp() {	
		
		creatureDto01 = new CreatureDto(creatureId01, creatureName01, creatureDescription01, areaId01);
		creatureDto02 = new CreatureDto(creatureId02, creatureName02, creatureDescription02, areaId01);
		
	}

	@Test
	void getAll_shouldReturnMonoList() {
		
		Mockito.when(sortingValidator.isValidSortField(Mockito.any(Class.class), Mockito.anyString())).thenReturn(false);
		Mockito.when(sortingValidator.getDefaultSortField(Mockito.any(Class.class))).thenReturn(GlobalConstants.CREATURE_ID);
		ArgumentCaptor<PaginationDto> paginationDtoCaptor = ArgumentCaptor.forClass(PaginationDto.class);
		Mockito.when(dtoValidator.validateDto(paginationDtoCaptor.capture())).thenAnswer(invocation -> Mono.just(paginationDtoCaptor.getValue()));
		Mockito.when(queryService.getAll(Mockito.any(Pageable.class))).thenReturn(Flux.just(creatureDto01, creatureDto02));
		
		webTestClient
		.get().uri(uriBuilder -> uriBuilder
	            .path(PATH)
	            .queryParam(GlobalConstants.PS_BY, GlobalConstants.MSG_COMMAND_ILLEGAL)
	            .queryParam(GlobalConstants.PS_DIR, GlobalConstants.PS_A)
	            .build())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(CreatureDto.class)
        .hasSize(2);

		Mockito.verify(sortingValidator).isValidSortField(Mockito.any(Class.class), Mockito.anyString());
		Mockito.verify(sortingValidator).getDefaultSortField(Mockito.any(Class.class));
		Mockito.verify(dtoValidator).validateDto(Mockito.any(PaginationDto.class));
		Mockito.verify(queryService).getAll(Mockito.any(Pageable.class));
		
	}

	@Test
	void getById_shouldReturnMonoEntity() {
		
		Mockito.when(queryService.getById(Mockito.any(UUID.class))).thenReturn(Mono.just(creatureDto01));

		webTestClient
		.get().uri(PATH + "/{id}", creatureId01)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(CreatureDto.class)
        .value( dto -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(dto).isNotNull(),
					() -> Assertions.assertThat(dto.creatureId()).isEqualTo(creatureId01),
					() -> Assertions.assertThat(dto.areaId()).isEqualTo(areaId01)			
					);			
		});
		
		Mockito.verify(queryService).getById(Mockito.any(UUID.class));

	}

	@Test
	void getById_WithIdInvalid_ReturnResponseWithErrorInfo() {
		
		Mono<ServerResponse> serverResponse = ServerResponse
				.status(HttpStatus.I_AM_A_TEAPOT)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createErrorResponse());
		Mockito.when(errorHandler.handleException(Mockito.any(Throwable.class))).thenReturn(serverResponse);
		
		webTestClient
		.get().uri(PATH + "/{id}", Collections.singletonMap("id", "bad-format"))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody(ErrorResponse.class)
		.value( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.name()).isEqualTo(GlobalConstants.EX_ILLEGALARGUMENT)
					);
		});
		
		Mockito.verify(errorHandler).handleException(Mockito.any(Throwable.class));
	}
	
	private ErrorResponse createErrorResponse() {
		
		return new ErrorResponse(
        				UUID.randomUUID(),
        				GlobalConstants.EX_ILLEGALARGUMENT,
        				GlobalConstants.EX_ILLEGALARGUMENT_DESC,
        				Map.of("","")
            		);
	}
	
}

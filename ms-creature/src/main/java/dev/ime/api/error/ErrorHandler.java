package dev.ime.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.application.dto.ErrorResponse;
import dev.ime.application.exception.*;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class ErrorHandler{

	private final Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> exceptionHandlers;	
	private final ReactiveLoggerUtils reactiveLoggerUtils;

    public ErrorHandler(ReactiveLoggerUtils reactiveLoggerUtils) {
        this.exceptionHandlers = initializeExceptionHandlers();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
    }
    
	private Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> initializeExceptionHandlers() {
		
		return Map.of(
				IllegalArgumentException.class, this::handleIllegalArgumentException,
				ValidationException.class, this::handleBasicExceptionExtendedClasses,
				UniqueValueException.class, this::handleBasicExceptionExtendedClasses,
				InvalidUUIDException.class, this::handleBasicExceptionExtendedClasses,
				ResourceNotFoundException.class, this::handleBasicExceptionExtendedClasses,
				EmptyResponseException.class, this::handleBasicExceptionExtendedClasses,
				CreateJpaEntityException.class, this::handleBasicExceptionExtendedClasses,
				CreateRedisEntityException.class, this::handleBasicExceptionExtendedClasses
				);		
	 }	 
	
    public Mono<ServerResponse> handleException(Throwable error) {
        
        return exceptionHandlers
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isInstance(error))
                .findFirst()
                .map(entry -> entry.getValue().apply(error))
                .orElseGet( () -> handleGenericException(error) );
        
    }
    
    private Mono<ServerResponse> handleGenericException(Throwable error) {
    	
        return ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                		new ErrorResponse(
	                		UUID.randomUUID(),
	                		error.getClass().getSimpleName(),
	                		GlobalConstants.EX_PLAIN_DESC, 
	                		Map.of(GlobalConstants.EX_PLAIN, error.getMessage())
                		))
		        .flatMap(response -> reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(),GlobalConstants.MSG_EVENT_ERROR, error.toString()).thenReturn(response));
    
    }

	public Mono<ServerResponse> handleIllegalArgumentException(Throwable error) {
		
		return ServerResponse
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(
		        		new ErrorResponse(
		                		UUID.randomUUID(),
		                		GlobalConstants.EX_ILLEGALARGUMENT,
		                		GlobalConstants.EX_ILLEGALARGUMENT_DESC, 
		                		Map.of(GlobalConstants.EX_PLAIN, error.getMessage())
		            		))		
		        .flatMap(response -> reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(),GlobalConstants.MSG_EVENT_ERROR, error.toString()).thenReturn(response));

	}
	
	private Mono<ServerResponse> handleBasicExceptionExtendedClasses(Throwable error) {
		
		BasicException ex = (BasicException)error;
		
		return createServerResponse(ex);
	}

	private Mono<ServerResponse> createServerResponse(BasicException error) {
		
		return ServerResponse
				.status(HttpStatus.I_AM_A_TEAPOT)
                .contentType(MediaType.APPLICATION_JSON)
				.bodyValue(
		        		new ErrorResponse(
		        				error.getIdentifier(),
		        				error.getName(),
		        				error.getDescription(), 
		        				error.getErrors()
		            		))
		        .flatMap(response -> reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(),GlobalConstants.MSG_EVENT_ERROR, error.toString()).thenReturn(response));

	}
    
	
}

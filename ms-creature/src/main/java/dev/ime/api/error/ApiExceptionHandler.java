package dev.ime.api.error;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import dev.ime.application.dto.ErrorResponse;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler{

	private final ReactiveLoggerUtils reactiveLoggerUtils;

	public ApiExceptionHandler(ReactiveLoggerUtils reactiveLoggerUtils) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
	}

	@ExceptionHandler(org.springframework.web.reactive.resource.NoResourceFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNoResourceFoundException(NoResourceFoundException ex) {        

        return Mono.just(ResponseEntity
		        		.status(HttpStatus.NOT_FOUND)
		        		.body(createNoResourceFoundExceptionErrorResponse(ex)))
        		.flatMap(response -> reactiveLoggerUtils.logSevereAction(GlobalConstants.EX_RESOURCENOTFOUND + " <==> " + ex.toString()).thenReturn(response));

    }

	private ErrorResponse createNoResourceFoundExceptionErrorResponse(NoResourceFoundException ex) {
		
		return new ErrorResponse(
            UUID.randomUUID(),
            GlobalConstants.EX_RESOURCENOTFOUND,
            GlobalConstants.EX_RESOURCENOTFOUND_DESC,
            Map.of(ex.getLocalizedMessage(), ex.getMessage())
        );
		 
	}
	
	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
	    
	    return Mono.just(ResponseEntity
				        .status(HttpStatus.INTERNAL_SERVER_ERROR)
				        .body(createHandleGenericExceptionErrorResponse(ex)))
	    		.flatMap(response -> reactiveLoggerUtils.logSevereAction(GlobalConstants.EX_PLAIN + " <==> " + ex.toString()).thenReturn(response));

	}

	private ErrorResponse createHandleGenericExceptionErrorResponse(Exception ex) {
		
		return new ErrorResponse(
	            UUID.randomUUID(),
	            GlobalConstants.EX_PLAIN,
	            GlobalConstants.EX_PLAIN_DESC,
	            Map.of(ex.getLocalizedMessage(), ex.getMessage())
	        );
		 
	}

}

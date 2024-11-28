package dev.ime.exceptionhandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.ime.config.GlobalConstants;
import dev.ime.dto.ErrorResponse;
import dev.ime.exception.BasicException;
import dev.ime.utils.LoggerUtils;

@ControllerAdvice
public class ApiExceptionHandler {
	
    private final LoggerUtils loggerUtil;

	public ApiExceptionHandler(LoggerUtils loggerUtil) {
		super();
		this.loggerUtil = loggerUtil;
	}
	
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex){

		loggerUtil.logSevereAction(GlobalConstants.EX_VALIDATION + GlobalConstants.MSG_SEPARATOR_SEVERE + ex.toString());
		
		Map<String, String> errors = new HashMap<>();
		    ex.getBindingResult().getAllErrors().forEach( error -> {
		        String fieldName = ((FieldError) error).getField();
		        String errorMessage = error.getDefaultMessage();
		        errors.put(fieldName, errorMessage);
		    });
		    
	    ErrorResponse response = new ErrorResponse(
	            UUID.randomUUID(),
	            GlobalConstants.EX_VALIDATION,
	            GlobalConstants.EX_VALIDATION_DESC,
	            errors
	        );
	    
		return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
	}	

	@ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> noResourceFoundException(Exception ex){
		
		loggerUtil.logSevereAction(GlobalConstants.EX_NORESOURCE + GlobalConstants.MSG_SEPARATOR_SEVERE + ex.toString());
	
		ErrorResponse response = new ErrorResponse(
	            UUID.randomUUID(),
	            GlobalConstants.EX_NORESOURCE,
	            GlobalConstants.EX_NORESOURCE_DESC,
	            Map.of(ex.getLocalizedMessage(), ex.getMessage())
	        );
	    
		return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
		
	}
	
	@ExceptionHandler({
		dev.ime.exception.EmailUsedException.class,
		dev.ime.exception.EmptyResponseException.class,
		dev.ime.exception.BasicException.class
		})	
	public ResponseEntity<ErrorResponse> handleBasicExceptionExtendedClasses(BasicException ex){

		loggerUtil.logSevereAction(GlobalConstants.EX_PLAIN + GlobalConstants.MSG_SEPARATOR_SEVERE + ex.toString());
		
		ErrorResponse response = new ErrorResponse(
				ex.getIdentifier(),
				ex.getName(),
				ex.getDescription(), 
				ex.getErrors()
    		);
		   
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}	

	@ExceptionHandler(Exception.class)		
	public ResponseEntity<ErrorResponse> lastExceptionStands(Exception ex){

		loggerUtil.logSevereAction(GlobalConstants.EX_PLAIN + GlobalConstants.MSG_SEPARATOR_SEVERE + ex.toString());
		
		ErrorResponse response = new ErrorResponse(
	            UUID.randomUUID(),
	            GlobalConstants.EX_PLAIN,
	            GlobalConstants.EX_PLAIN_DESC,
	            Map.of(ex.getLocalizedMessage(), ex.getMessage())
	        );
		   
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}	

}

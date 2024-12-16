package dev.ime.api.validation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import dev.ime.application.exception.ValidationException;
import reactor.core.publisher.Mono;

@Component
public class DtoValidator {
	
	private final Validator validator;
	 
    public DtoValidator(Validator validator) {
		super();
		this.validator = validator;
	}

	public <T> Mono<T> validateDto(T dto) {
		
	    Errors errors = new BeanPropertyBindingResult(dto, dto.getClass().getSimpleName());
	    validator.validate(dto, errors);

	    if (errors.hasErrors()) {
	    	
	    	Map<String, String> errorMap = errorsMapper(errors);
	    	
	    	return Mono.error(new ValidationException(errorMap));
	        
	    }

	    return Mono.just(dto);
	    
	}

	private Map<String, String> errorsMapper(Errors errors) {
		
		Map<String, String> errorMap = new HashMap<>();
		
		errors.getAllErrors().forEach(error -> {
			
		    String fieldName = ((FieldError) error).getField();
		    String errorMessage = error.getDefaultMessage();
		    errorMap.put(fieldName, errorMessage);
		    
		});
		
		return errorMap;
		
	}
	
}

package dev.ime.application.utils;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.AreaDto;
import dev.ime.domain.model.Area;

@Component
public class SortingValidationUtils {

	private final Map<Class<?>, Set<String>> validSortFieldsMap;
	private final ReflectionUtils reflectionUtils;
	
	public SortingValidationUtils(ReflectionUtils reflectionUtils) {
		super();
		this.reflectionUtils = reflectionUtils;
		this.validSortFieldsMap = initializeMap();
	}
	
	private Map<Class<?>, Set<String>> initializeMap() {
		
		return Map.of(
				AreaDto.class, reflectionUtils.getFieldNames(Area.class)
				);		
	}
	
    public String getDefaultSortField(Class<?> keyClass) {
    	
    	Set<String> validFieldsSet =  validSortFieldsMap
        		.get(keyClass);
    	
        if (validFieldsSet == null || validFieldsSet.isEmpty()) {
            return "";
        }
        
        String expectedIdField = getExpectedIdField(keyClass);

    	return validFieldsSet
        		.stream()
        		.filter( fieldName -> fieldName.toLowerCase().equals(expectedIdField))
        		.findFirst()
        		.orElse(validFieldsSet.iterator().next());        		
        		
    }
    
    private String getExpectedIdField(Class<?> keyClass) {
    	
    	String sufixDto = keyClass.getSimpleName().toLowerCase();
    	String sufixClass = sufixDto.substring(0, sufixDto.length() - 3);
    	
        return sufixClass + "id";

    }
    
    public boolean isValidSortField(Class<?> keyClass, String sortField) {
    	
    	if ( !isValidKeyclass(keyClass) ) {
    		return false;
    	}
    	
    	return  validSortFieldsMap
        		.get(keyClass)
        		.stream()
        		.anyMatch( fieldName -> fieldName.equals(sortField));    
        
    }

    private boolean isValidKeyclass(Class<?> keyClass) {
    	
        return validSortFieldsMap
        		.containsKey(keyClass);
        
    }
 
}

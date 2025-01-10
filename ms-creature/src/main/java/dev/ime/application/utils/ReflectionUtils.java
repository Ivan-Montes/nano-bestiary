package dev.ime.application.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ReflectionUtils {	
	
	public ReflectionUtils() {
		super();
	}

	public Set<String> getFieldNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                     .map(Field::getName)
                     .collect(Collectors.toSet());
        
    }
	
}

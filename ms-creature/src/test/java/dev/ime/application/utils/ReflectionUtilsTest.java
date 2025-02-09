package dev.ime.application.utils;


import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.domain.model.Creature;

@ExtendWith(MockitoExtension.class)
class ReflectionUtilsTest {

	@InjectMocks
	private ReflectionUtils reflectionUtils;
	
	@Test
	void getFieldNames_shouldReturnList() {
		
		Set<String> fielNamesSet = reflectionUtils.getFieldNames(Creature.class);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(fielNamesSet).isNotNull(),
				()-> Assertions.assertThat(fielNamesSet).isNotEmpty()
				);
	}

}

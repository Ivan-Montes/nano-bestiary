package dev.ime.application.utils;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.dto.AreaDto;
import dev.ime.application.dto.PaginationDto;
import dev.ime.config.GlobalConstants;

@ExtendWith(MockitoExtension.class)
class SortingValidationUtilsTest {
	
	@Spy
	private ReflectionUtils reflectionUtils;
	
    @InjectMocks
    private SortingValidationUtils sortingValidationUtils;
    
	@Test
	void getDefaultSortField_shouldReturnIdField() {
		
		String result = sortingValidationUtils.getDefaultSortField(AreaDto.class);
		
		org.junit.jupiter.api.Assertions.assertAll(
        		() -> Assertions.assertThat(result).isNotNull(),
        		() -> Assertions.assertThat(result).isEqualTo(GlobalConstants.AREA_ID)
        		);
	}
	
	@Test
	void isValidSortField_shouldReturnTrue() {
		
		boolean result = sortingValidationUtils.isValidSortField(AreaDto.class, GlobalConstants.AREA_ID);
		
		org.junit.jupiter.api.Assertions.assertAll(
        		() -> Assertions.assertThat(result).isTrue()
				);
		
	}

	@Test
	void isValidSortField_shouldReturnFalse() {
		
		boolean result = sortingValidationUtils.isValidSortField(PaginationDto.class, GlobalConstants.AREA_ID);
		
		org.junit.jupiter.api.Assertions.assertAll(
        		() -> Assertions.assertThat(result).isFalse()
				);
		
	}
	
}

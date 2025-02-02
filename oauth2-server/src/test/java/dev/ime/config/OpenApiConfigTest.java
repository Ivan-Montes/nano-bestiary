package dev.ime.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import io.swagger.v3.oas.models.OpenAPI;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

	@InjectMocks
	private OpenApiConfig openApiConfig;
	
	@Test
	void customOpenAPI_getBean_ReturnBean() {
		
		OpenAPI openApi = openApiConfig.customOpenAPI();
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(openApi).isNotNull()
				);
	}

}

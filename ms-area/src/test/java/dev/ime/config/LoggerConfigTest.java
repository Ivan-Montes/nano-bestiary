package dev.ime.config;


import java.util.logging.Logger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoggerConfigTest {

	@InjectMocks
	private LoggerConfig loggerConfig;
	
	@Test
	void loggerBean_ReturnLogger() {
		
		Logger loggerBean = loggerConfig.loggerBean();
		
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(loggerBean).isNotNull()
				);
		
	}

}

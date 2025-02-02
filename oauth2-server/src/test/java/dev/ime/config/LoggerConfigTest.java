package dev.ime.config;


import java.util.logging.Logger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LoggerConfigTest {

	private final LoggerConfig loggerConfig = new LoggerConfig();
	
	@Test
	void loggerBean_ByDefault_ReturnLogger() {
		
		Logger log = loggerConfig.loggerBean();
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(log).isNotNull()
				);	
	}

}

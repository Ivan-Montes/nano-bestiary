package dev.ime.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfig {

	@Bean
	Logger loggerBean() {
		return Logger.getLogger(getClass().getName());
	}
	
}

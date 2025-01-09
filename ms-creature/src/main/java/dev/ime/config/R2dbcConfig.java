package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

@Configuration
public class R2dbcConfig {

	 @Bean
	 R2dbcEntityTemplate r2dbcEntityTemplate(R2dbcConfigProperties r2dbcConfigProperties) {
		 
		 ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
				   .option(ConnectionFactoryOptions.DRIVER, r2dbcConfigProperties.getDriver())
				   .option(ConnectionFactoryOptions.HOST, r2dbcConfigProperties.getHost())
				   .option(ConnectionFactoryOptions.PORT, Integer.valueOf( r2dbcConfigProperties.getPort() ))
				   .option(ConnectionFactoryOptions.USER, r2dbcConfigProperties.getUsername())
				   .option(ConnectionFactoryOptions.PASSWORD, r2dbcConfigProperties.getPassword())
				   .option(ConnectionFactoryOptions.DATABASE, r2dbcConfigProperties.getDatabase())
				   .build());

	        return new R2dbcEntityTemplate(connectionFactory);
	        
	    }
	 
}

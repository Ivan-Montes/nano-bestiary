package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class R2dbcConfigPropertiesTest {

	private R2dbcConfigProperties r2dbcConfigProperties;
	
	private final String username = GlobalConstants.EX_UNIQUEVALUE;
	private final String password = GlobalConstants.MSG_UNKNOWDATA;
	private final String database = GlobalConstants.MSG_NODATA;
	private final String host = GlobalConstants.EX_RESOURCENOTFOUND;
	private final String port = GlobalConstants.EX_PLAIN;
	private final String driver = GlobalConstants.EX_EMPTYRESPONSE;
	
	@Test
	void testSettersAndGetters() {
		
		r2dbcConfigProperties = new R2dbcConfigProperties();
		r2dbcConfigProperties.setUsername(username);
		r2dbcConfigProperties.setPassword(password);
		r2dbcConfigProperties.setDatabase(database);
		r2dbcConfigProperties.setHost(host);
		r2dbcConfigProperties.setPort(port);
		r2dbcConfigProperties.setDriver(driver);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(r2dbcConfigProperties).isNotNull(),
				()-> Assertions.assertThat(r2dbcConfigProperties.getUsername()).isEqualTo(username),
				()-> Assertions.assertThat(r2dbcConfigProperties.getPassword()).isEqualTo(password),
				()-> Assertions.assertThat(r2dbcConfigProperties.getDatabase()).isEqualTo(database),
				()-> Assertions.assertThat(r2dbcConfigProperties.getHost()).isEqualTo(host),
				()-> Assertions.assertThat(r2dbcConfigProperties.getPort()).isEqualTo(port),
				()-> Assertions.assertThat(r2dbcConfigProperties.getDriver()).isEqualTo(driver)				
				);
	}

}

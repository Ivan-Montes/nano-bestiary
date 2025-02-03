package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AuthorizationServerPropertiesTest {

	private final String issuer = "http://loopback:7777";
	
	@Test
	void getterSetter_WithSetter_createObject() {
		
		AuthorizationServerProperties authorizationServerProperties = new AuthorizationServerProperties();
		authorizationServerProperties.setIssuer(issuer);		
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(authorizationServerProperties).isNotNull(),
				()-> Assertions.assertThat(authorizationServerProperties.getIssuer()).isEqualTo(issuer)
				);
	}

}

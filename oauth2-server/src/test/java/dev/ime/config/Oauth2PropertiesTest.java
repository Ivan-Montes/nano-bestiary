package dev.ime.config;


import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class Oauth2PropertiesTest {

	private String clientid = UUID.randomUUID().toString();
	private String clientsecret = UUID.randomUUID().toString();
	private String redirectUri = "https://loopback:port";
	
	@Test
	void oauth2Properties_WithSetter_CreateObject() {
		
		Oauth2Properties oauth2Properties = new Oauth2Properties();
		oauth2Properties.setClientId(clientid);
		oauth2Properties.setClientSecret(clientsecret);
		oauth2Properties.setRedirectUri(redirectUri);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(oauth2Properties).isNotNull(),
				()-> Assertions.assertThat(oauth2Properties.getClientId()).isEqualTo(clientid),
				()-> Assertions.assertThat(oauth2Properties.getClientSecret()).isEqualTo(clientsecret),
				()-> Assertions.assertThat(oauth2Properties.getRedirectUri()).isEqualTo(redirectUri)
				);
	}

}

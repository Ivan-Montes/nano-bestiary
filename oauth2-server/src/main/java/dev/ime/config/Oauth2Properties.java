package dev.ime.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "properties.oauth2")
public class Oauth2Properties {

	private String clientId;
	private String clientSecret;
	private String redirectUri;
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientid) {
		this.clientId = clientid;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientsecret) {
		this.clientSecret = clientsecret;
	}
	public String getRedirectUri() {
		return redirectUri;
	}
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	
}

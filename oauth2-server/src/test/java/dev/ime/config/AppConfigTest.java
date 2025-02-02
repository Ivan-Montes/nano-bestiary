package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import dev.ime.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AppConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorizationServerProperties authorizationServerProperties;

    @InjectMocks
    private AppConfig appConfig;
    
    
    
	@Test
	void userDetailsService_WithMockParam_ReturnUserDetailsService() {
		
		UserDetailsService result = appConfig.userDetailsService(userRepository);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isNotNull()
				);
	}

	@Test
	void passwordEncoder_ByDefault_returnEncoder() {
		
		PasswordEncoder result = appConfig.passwordEncoder();

		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isNotNull()
				);
	}
	
	@Test
	void authorizationServerSettingsWithIssuer_WithMockParam_ReturnAuthorizationServerSettings() {
		
		Mockito.when(authorizationServerProperties.getIssuer()).thenReturn("https://issuer.es");
		
		AuthorizationServerSettings result = appConfig.authorizationServerSettingsWithIssuer(authorizationServerProperties);
	
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isNotNull()
				);
	}
	

}

package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@ExtendWith(MockitoExtension.class)
class OAuth2ConfigTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Oauth2Properties oauth2Properties;

    @Mock
    private JdbcTemplate jdbcTemplate;
    
    @Mock
    private RegisteredClientRepository registeredClientRepository;
    
    @InjectMocks
    private OAuth2Config oauth2Config;
    
    @Test
    void registeredClientRepository_WithMockParams_ReturnRegisteredClientRepository() {
    	
        Mockito.when(oauth2Properties.getClientId()).thenReturn("testClientId");
        Mockito.when(oauth2Properties.getClientSecret()).thenReturn("testClientSecret");
        Mockito.when(oauth2Properties.getRedirectUri()).thenReturn("http://here:8888");
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");

        RegisteredClientRepository result = oauth2Config.registeredClientRepository(
                passwordEncoder, oauth2Properties, jdbcTemplate);

    	org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isNotNull(),
				()-> Assertions.assertThat(result instanceof JdbcRegisteredClientRepository)
				);
        Mockito.verify(oauth2Properties, Mockito.times(2)).getClientId();
        Mockito.verify(oauth2Properties).getClientSecret();
        Mockito.verify(passwordEncoder).encode(Mockito.anyString());
    }

    @Test
    void authorizationService_WithMockParams_ReturnOAuth2AuthorizationService() {
    	
    	OAuth2AuthorizationService result = oauth2Config.authorizationService(jdbcTemplate, registeredClientRepository);
    	
    	org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isNotNull(),
				()-> Assertions.assertThat(result instanceof OAuth2AuthorizationService)
				);
    }
    
}

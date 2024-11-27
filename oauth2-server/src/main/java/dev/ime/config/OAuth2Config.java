package dev.ime.config;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.model.Role;
import dev.ime.model.User;

@Configuration
public class OAuth2Config {

    @Bean 
    RegisteredClientRepository registeredClientRepository(
            PasswordEncoder passwordEncoder, 
            Oauth2Properties oauth2Properties, 
            JdbcTemplate jdbcTemplate) {
        
        JdbcRegisteredClientRepository registeredClientRepository = 
            new JdbcRegisteredClientRepository(jdbcTemplate);

        ensureClientExists(registeredClientRepository, passwordEncoder, oauth2Properties);

        return registeredClientRepository;
    }

    private void ensureClientExists(JdbcRegisteredClientRepository repository, 
                                    PasswordEncoder passwordEncoder, 
                                    Oauth2Properties oauth2Properties) {
        if (repository.findByClientId(oauth2Properties.getClientId()) == null) {
            RegisteredClient newClient = createRegisteredClient(passwordEncoder, oauth2Properties);
            repository.save(newClient);
        }
    }
    
	private RegisteredClient createRegisteredClient(PasswordEncoder passwordEncoder, Oauth2Properties oauth2Properties) {
		
		return RegisteredClient.withId(UUID.randomUUID().toString())
		    .clientId(oauth2Properties.getClientId())
		    .clientSecret(passwordEncoder.encode(oauth2Properties.getClientSecret()))
		    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
		    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
		    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
		    .redirectUri(oauth2Properties.getRedirectUri())
		    .scope(OidcScopes.OPENID)
		    .clientSettings(ClientSettings.builder()
		        .requireAuthorizationConsent(true)
		        .build())
		    .tokenSettings(TokenSettings.builder()
		        .accessTokenTimeToLive(Duration.ofMinutes(30))
		        .refreshTokenTimeToLive(Duration.ofDays(30))
		        .build())
		    .build();
		
	}
	
    @Bean
    OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        
    	JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
        configureAuthorizationRowMapper(service, registeredClientRepository);
        return service;
        
    }

    private void configureAuthorizationRowMapper(JdbcOAuth2AuthorizationService service, RegisteredClientRepository registeredClientRepository) {
        
    	JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper = 
            new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository);
        
        ObjectMapper objectMapper = createAndConfigureObjectMapper();
        rowMapper.setObjectMapper(objectMapper);
        service.setAuthorizationRowMapper(rowMapper);
        
    }

    private ObjectMapper createAndConfigureObjectMapper() {
    	
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<com.fasterxml.jackson.databind.Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.addMixIn(User.class, UserMixin.class);
        return objectMapper;
        
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private abstract class UserMixin {
        @JsonCreator
        UserMixin(@JsonProperty("id") Long id,
                  @JsonProperty("name") String name,
                  @JsonProperty("lastname") String lastname,
                  @JsonProperty("email") String email,
                  @JsonProperty("password") String password,
                  @JsonProperty("role") Role role) {}

        @JsonIgnore abstract String getUsername();
        @JsonIgnore abstract String getPassword();
        @JsonIgnore abstract Collection<GrantedAuthority> getAuthorities();
        @JsonIgnore abstract boolean isAccountNonExpired();
        @JsonIgnore abstract boolean isAccountNonLocked();
        @JsonIgnore abstract boolean isCredentialsNonExpired();
        @JsonIgnore abstract boolean isEnabled();
    }
    
}

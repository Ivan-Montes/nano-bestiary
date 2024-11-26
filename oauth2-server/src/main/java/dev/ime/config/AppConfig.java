package dev.ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import dev.ime.repository.UserRepository;

@Configuration
public class AppConfig {

	@Bean
	UserDetailsService userDetailsService(UserRepository userRepository) {
		
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
				
				return userRepository.findByEmail(userName)
						.orElseThrow( () -> new UsernameNotFoundException(GlobalConstants.EX_USERNOTFOUND));
			}			
		};		
	}
	
    @Bean
    PasswordEncoder passwordEncoder() {
    	
        return new BCryptPasswordEncoder();
        
    }
    
    @Bean
    AuthorizationServerSettings authorizationServerSettingsWithIssuer(AuthorizationServerProperties authorizationServerProperties) {
        return AuthorizationServerSettings
        		.builder()
                .issuer(authorizationServerProperties.getIssuer())
                .build();
    }

}

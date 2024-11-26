package dev.ime.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

@Configuration
public class JwtConfig {
	
	@Bean
	JWKSource<SecurityContext> jwkSource() {
	    KeyPair keyPair = generateRsaKey();
	    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	    RSAKey rsaKey = new RSAKey.Builder(publicKey)
	            .privateKey(privateKey)
	            .keyID(UUID.randomUUID().toString())
	            .build();
	    JWKSet jwkSet = new JWKSet(rsaKey);
	    return new ImmutableJWKSet<>(jwkSet);
	}
	
	private KeyPair generateRsaKey() {
	        KeyPair keyPair;
	        try {
	            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	            keyPairGenerator.initialize(2048);
	            keyPair = keyPairGenerator.generateKeyPair();
	        } catch (Exception ex) {
	            throw new IllegalStateException(ex);
	        }
	        return keyPair;
	    }
	
	@Bean
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
	
	@Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
	    
}

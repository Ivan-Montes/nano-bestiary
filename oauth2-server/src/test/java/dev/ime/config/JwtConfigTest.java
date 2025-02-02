package dev.ime.config;


import java.security.KeyPairGenerator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@ExtendWith(MockitoExtension.class)
class JwtConfigTest {
	
	@InjectMocks
	private JwtConfig jwtConfig;
	
	private JWKSource<SecurityContext> jwkSource;
	
	@BeforeEach
	private void setUp() {
		
		jwkSource = jwtConfig.jwkSource();	
		
	}
	
	@Test
	void jwtDecoder_WithParam_ReturnDecoder() {
		
		JwtDecoder jwtDecoder = jwtConfig.jwtDecoder(jwkSource);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(jwtDecoder).isNotNull()
				);		
	}

	@Test
	void jwtEncoder_WithParam_ReturnEncoder() {
		
		JwtEncoder jwtEncoder = jwtConfig.jwtEncoder(jwkSource);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(jwtEncoder).isNotNull()
				);		
	}

	@Test
	void generateRsaKey_WithError_ThrowEx() {
		
		try ( MockedStatic<KeyPairGenerator> keyPairGeneratorStaticUtils = Mockito.mockStatic(KeyPairGenerator.class)){
			
			keyPairGeneratorStaticUtils.when( ()-> KeyPairGenerator.getInstance("RSA")).thenReturn(null);
			
			Exception ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> jwtConfig.jwkSource());
			
			org.junit.jupiter.api.Assertions.assertAll(
					()-> Assertions.assertThat(ex).isNotNull(),
					()-> Assertions.assertThat(ex.getClass()).isEqualTo(IllegalStateException.class)
					);
		}
	}
	
}

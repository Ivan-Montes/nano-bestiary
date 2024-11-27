package dev.ime.utils;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;
import dev.ime.model.User;

@Component
public class JwtUtils {

    private final JwtEncoder jwtEncoder;
    private final AuthorizationServerSettings authorizationServerSettings;

    public JwtUtils(JwtEncoder jwtEncoder, AuthorizationServerSettings authorizationServerSettings) {
		super();
		this.jwtEncoder = jwtEncoder;
		this.authorizationServerSettings = authorizationServerSettings;
	}

	public String generateToken(User user) {
		
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(authorizationServerSettings.getIssuer())
        		.issuedAt(now)
                .expiresAt(now.plusSeconds(GlobalConstants.EXPIRATION_TIME))
                .subject(user.getEmail())
                .claim("name", user.getName())
                .claim("lastname", user.getLastname())
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        
    }
    
}

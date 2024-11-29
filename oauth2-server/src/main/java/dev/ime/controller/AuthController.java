package dev.ime.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.ime.exception.EmptyResponseException;
import dev.ime.config.GlobalConstants;
import dev.ime.dto.RegisterRequestDto;
import dev.ime.dto.UserDto;
import dev.ime.service.AuthService;
import jakarta.validation.Valid;

@RestController
public class AuthController {

	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		super();
		this.authService = authService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto){
		
		return ResponseEntity
				.ok(authService.register(registerRequestDto)
						.orElseThrow( () -> new EmptyResponseException(Map.of(
								GlobalConstants.USER_CAT, GlobalConstants.MSG_NODATA
            ))));	
		
	}
	
}

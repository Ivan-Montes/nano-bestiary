package dev.ime.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.ime.exception.EmailUsedException;
import dev.ime.config.GlobalConstants;
import dev.ime.dto.RegisterRequestDto;
import dev.ime.dto.UserDto;
import dev.ime.model.Role;
import dev.ime.model.User;
import dev.ime.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public Optional<UserDto> register(RegisterRequestDto registerRequestDto) {
		
	    validateEmailAvailability(registerRequestDto.email());
	
		User user = User.builder()
				.name(registerRequestDto.name())
				.lastname(registerRequestDto.lastname())
				.email(registerRequestDto.email())
				.password(passwordEncoder.encode( registerRequestDto.password() ))
				.role(Role.USER)
				.build();
		
		User userSaved = userRepository.save(user);
		
		return Optional.ofNullable(userSaved)
				.map(this::createUserDto);
		
	}

	private UserDto createUserDto(User user) {
		
		return new UserDto(user.getEmail(), user.getPassword());
		
	}
	
	private void validateEmailAvailability(String email) {
		
	    if (userRepository.findByEmail(email).isPresent()) {
	    	
	        throw new EmailUsedException(Map.of(GlobalConstants.USER_EMAIL, email));
	    
	    }
	}
}

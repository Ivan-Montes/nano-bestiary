package dev.ime.service;


import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.ime.dto.RegisterRequestDto;
import dev.ime.dto.UserDto;
import dev.ime.exception.EmailUsedException;
import dev.ime.model.User;
import dev.ime.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
    private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private AuthService authService;	

	private RegisterRequestDto registerRequestDto;
	private User user;
	private final String name = "B2";
	private final String lastname = "Yorha";
	private final String email = "email@domain.tk";
	private final String password = "pass";
	
	@BeforeEach
	private void setUp() {
		
		registerRequestDto = new RegisterRequestDto(
				name,
				lastname,
				email,
				password);
		
		user = new User();
		user.setEmail(email);
		user.setPassword(password);
		
	}
		
	@Test
	void register_WithRegisterRequest_ReturnOptUserDto() {

		Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.ofNullable(null));
		Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(UUID.randomUUID().toString());
		Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
		
		Optional<UserDto> optUserDto = authService.register(registerRequestDto);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(optUserDto).isNotEmpty(),
				()-> Assertions.assertThat(optUserDto.get()).isNotNull(),
				()-> Assertions.assertThat(optUserDto.get().email()).isEqualTo(email),
				()-> Assertions.assertThat(optUserDto.get().password()).isEqualTo(password)
				);
	}

	@Test
	void register_WithRepositoryReturnNull_ThrowException() {

		Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.ofNullable(new User()));
		
		Exception ex = org.junit.jupiter.api.Assertions.assertThrows(EmailUsedException.class, ()-> authService.register(registerRequestDto));
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(ex).isNotNull(),
				()-> Assertions.assertThat(ex.getClass()).isEqualTo(EmailUsedException.class)
				);
	}

}

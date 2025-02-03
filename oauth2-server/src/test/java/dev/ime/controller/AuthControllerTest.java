package dev.ime.controller;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.dto.RegisterRequestDto;
import dev.ime.dto.UserDto;
import dev.ime.exception.EmptyResponseException;
import dev.ime.service.AuthService;
import dev.ime.utils.LoggerUtils;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@MockitoBean
	private AuthService authService;
 
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;
	
	@MockitoBean
	private LoggerUtils loggerUtil;	

	private static final String PATH = "/register";	
	private RegisterRequestDto registerRequestDto;
	private UserDto userDto;
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
		
		userDto = new UserDto(email, password);
		
	}

	@Test
	void register_WithServiceReturnNull_ThrowException() throws Exception {
		
		Mockito.when(authService.register(Mockito.any(RegisterRequestDto.class))).thenReturn(Optional.ofNullable(null));
		
		mockMvc.perform(MockMvcRequestBuilders.post(PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequestDto)))
		.andExpect(MockMvcResultMatchers.status().is4xxClientError())
	    .andExpect(result -> Assertions.assertThat(result.getResolvedException() instanceof EmptyResponseException))
		;
		
	}

	@Test
	void register_WithRegisterRequest_ReturnUserDto() throws Exception {
		
		Mockito.when(authService.register(Mockito.any(RegisterRequestDto.class))).thenReturn(Optional.ofNullable(userDto));
		
		mockMvc.perform(MockMvcRequestBuilders.post(PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequestDto)))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.email", org.hamcrest.Matchers.equalTo(email)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.password").isNotEmpty())
		;
		
	}

}

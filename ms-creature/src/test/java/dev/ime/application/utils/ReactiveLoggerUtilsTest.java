package dev.ime.application.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.config.GlobalConstants;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ReactiveLoggerUtilsTest {

	@Mock
	private Logger logger;
	
	@InjectMocks
	private ReactiveLoggerUtils reactiveLoggerUtils;
	
	@Test
	void logSevereAction_ShouldReturnMonoVoid() {
		
		StepVerifier
		.create(reactiveLoggerUtils.logSevereAction(GlobalConstants.PATTERN_UUID_ZERO))
		.verifyComplete();
		Mockito.verify(logger).log(Mockito.any(Level.class), Mockito.anyString());

	}

	@Test
	void logSevereAction_ShouldHandleErrorAndReturnMonoVoid() {
	    
	    Mockito.doThrow(new RuntimeException(GlobalConstants.EX_PLAIN))
	           .when(logger)
	           .log(Mockito.any(Level.class), Mockito.anyString());

	    StepVerifier
	        .create(reactiveLoggerUtils.logSevereAction(GlobalConstants.PATTERN_UUID_ZERO))
	        .verifyComplete();

	    Mockito.verify(logger).log(Mockito.any(Level.class), Mockito.anyString());
	}
	@Test
	void logInfoAction_ShouldReturnMonoVoid() {
		
		StepVerifier
		.create(reactiveLoggerUtils.logInfoAction(
				GlobalConstants.PATTERN_NAME_FULL,
				GlobalConstants.PATTERN_DESC_FULL,
				GlobalConstants.PATTERN_UUID_ZERO)
				)
		.verifyComplete();
		Mockito.verify(logger).log(Mockito.any(Level.class), Mockito.anyString());

	}
	
	@Test
	void logInfoAction_ShouldHandleErrorAndReturnMonoVoid() {
	    
	    Mockito.doThrow(new RuntimeException(GlobalConstants.EX_PLAIN))
	           .when(logger)
	           .log(Mockito.any(Level.class), Mockito.anyString());

	    StepVerifier
	    .create(reactiveLoggerUtils.logInfoAction(
				GlobalConstants.PATTERN_NAME_FULL,
				GlobalConstants.PATTERN_DESC_FULL,
				GlobalConstants.PATTERN_UUID_ZERO)
				)
	    .verifyComplete();

	    Mockito.verify(logger).log(Mockito.any(Level.class), Mockito.anyString());
	}

}

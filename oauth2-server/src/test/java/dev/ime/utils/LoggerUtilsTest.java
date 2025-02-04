package dev.ime.utils;


import static org.mockito.Mockito.times;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoggerUtilsTest {

	@Mock
	private Logger logger;

	@InjectMocks
	private LoggerUtils loggerUtil;	
	
	@Test
	void logSevereAction_ByDefault_LogText() {
		
		Mockito.doNothing().when(logger).log(Mockito.any(Level.class), Mockito.anyString());
		
		loggerUtil.logSevereAction("Achtung");
		Mockito.verify(logger, times(1)).log(Mockito.any(Level.class), Mockito.anyString());
	}

	@Test
	void logInfoAction_ByDefault_LogText() {
		
		Mockito.doNothing().when(logger).log(Mockito.any(Level.class), Mockito.anyString());

		loggerUtil.logInfoAction("Achtung", "LoggerUtil", "Test");
		Mockito.verify(logger, times(1)).log(Mockito.any(Level.class), Mockito.anyString());
	}

}

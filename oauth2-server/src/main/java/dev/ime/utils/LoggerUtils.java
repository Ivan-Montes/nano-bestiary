package dev.ime.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;

@Component
public class LoggerUtils {

	private final Logger logger;

	public LoggerUtils(Logger logger) {
		super();
		this.logger = logger;
	}	
	
	public void logSevereAction(String msg) {
		
		String logMessage = String.format(GlobalConstants.MSG_PATTERN_SEVERE, msg);
		
		logger.log(Level.SEVERE, logMessage);
		
	} 

	public void logInfoAction(String className, String methodName, String msg) {
		
		String logMessage = String.format(GlobalConstants.MSG_PATTERN_INFO, className, methodName, msg);
		
		logger.log(Level.INFO, logMessage);
		
	}
		
}

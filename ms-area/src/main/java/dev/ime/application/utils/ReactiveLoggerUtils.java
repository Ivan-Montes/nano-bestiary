package dev.ime.application.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import dev.ime.config.GlobalConstants;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class ReactiveLoggerUtils {

	private final Logger logger;

	public ReactiveLoggerUtils(Logger logger) {
		super();
		this.logger = logger;
	}
	
	public Mono<Void> logSevereAction(String msg) {
		
		return Mono.fromRunnable( () -> 
		logger.log(
				Level.SEVERE,
				String.format(GlobalConstants.MSG_PATTERN_SEVERE, msg)
				)
		)
		.subscribeOn(Schedulers.boundedElastic())
		.onErrorResume( e -> Mono.empty())
		.then();
		
	} 

	public Mono<Void> logInfoAction(String className, String methodName, String msg) {
				
		return Mono.fromRunnable( () -> 
		logger.log(
				Level.INFO, 
				String.format(GlobalConstants.MSG_PATTERN_INFO, className, methodName, msg)
				)
		)
		.subscribeOn(Schedulers.boundedElastic())
		.onErrorResume( e -> Mono.empty())
		.then();
		
	}
	
}

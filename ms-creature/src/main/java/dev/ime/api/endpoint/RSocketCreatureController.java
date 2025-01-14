package dev.ime.api.endpoint;

import java.util.Map;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.ports.inbound.RSocketControllerPort;
import dev.ime.domain.ports.inbound.RSocketServicePort;
import reactor.core.publisher.Mono;

@Controller
public class RSocketCreatureController implements RSocketControllerPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
	private final RSocketServicePort rSocketServicePort;
	
	public RSocketCreatureController(ReactiveLoggerUtils reactiveLoggerUtils, RSocketServicePort rSocketServicePort) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.rSocketServicePort = rSocketServicePort;
	}

	@MessageMapping("creatures.existsAnyByAreaId.{id}")
	@Override
	public Mono<Boolean> existsAnyByAreaId(@DestinationVariable("id") String areaId) {

		return Mono.justOrEmpty(areaId)
				.map(UUID::fromString)
				.onErrorMap( e -> new InvalidUUIDException(Map.of(GlobalConstants.AREA_ID, GlobalConstants.MSG_NODATA)))
				.flatMap(this::existsAnyByAreaIdInDatabase)
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.AREA_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::addLogginOptions);

	}

	private Mono<Boolean> existsAnyByAreaIdInDatabase(UUID id){
		
		return rSocketServicePort
				.existsAnyByAreaId(id);
		
	}

	private <T> Mono<T> addLogginOptions(Mono<T> reactiveFlow){
		
		return reactiveFlow
				.doOnSubscribe( subscribed -> this.logInfo( GlobalConstants.MSG_FLOW_SUBS, subscribed.toString()) )
				.doOnSuccess( success -> this.logInfo( GlobalConstants.MSG_FLOW_OK, createExtraInfo(success) ))
	            .doOnCancel( () -> this.logInfo( GlobalConstants.MSG_FLOW_CANCEL, GlobalConstants.MSG_NODATA) )
	            .doOnError( error -> this.logInfo( GlobalConstants.MSG_FLOW_ERROR, error.toString()) )
		        .doFinally( signal -> this.logInfo( GlobalConstants.MSG_FLOW_RESULT, signal.toString()) );		
			
	}
	
	private void logInfo(String action, String extraInfo) {
		
    	reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), action, extraInfo).subscribe();

	}
	
	private <T> String createExtraInfo(T response) {
		
		return response instanceof Number? GlobalConstants.MSG_MODLINES + response.toString():response.toString();
				
	}

}

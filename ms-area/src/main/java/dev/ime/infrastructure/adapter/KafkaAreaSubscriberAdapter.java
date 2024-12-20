package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.ValidationException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.outbound.BaseProjectorPort;
import dev.ime.domain.ports.outbound.ExtendedProjectorPort;
import dev.ime.domain.ports.inbound.SubscriberPort;
import reactor.core.publisher.Mono;

@Component
public class KafkaAreaSubscriberAdapter implements SubscriberPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
    private final Map<String, Function<Event, Mono<?>>> actionsMap;  
    private final BaseProjectorPort baseProjectorPort;
    private final ExtendedProjectorPort extendedProjectorPort;
    
	public KafkaAreaSubscriberAdapter(ReactiveLoggerUtils reactiveLoggerUtils, @Qualifier("areaProjectorAdapter")BaseProjectorPort baseProjectorPort, @Qualifier("areaProjectorAdapter")ExtendedProjectorPort extendedProjectorPort) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.baseProjectorPort = baseProjectorPort;
		this.extendedProjectorPort = extendedProjectorPort;
		this.actionsMap = initializeActionsMap();
	}

	private Map<String, Function<Event, Mono<?>>> initializeActionsMap() {
		
		return Map.of(
                GlobalConstants.AREA_CREATED, baseProjectorPort::create,
                GlobalConstants.AREA_UPDATED, extendedProjectorPort::update,
                GlobalConstants.AREA_DELETED, baseProjectorPort::deleteById
        );
		
	}

	@KafkaListener(topics = {GlobalConstants.AREA_CREATED, GlobalConstants.AREA_UPDATED, GlobalConstants.AREA_DELETED}, groupId = "msarea-consumer-area")
	@Override
	public Mono<Void> onMessage(ConsumerRecord<String, Event> consumerRecord) {

		return Mono.just(consumerRecord)
				.transform(this::logFlowStep)
				.flatMap(this::validateTopic)
				.flatMap(this::validateValue)
				.flatMap(this::processEvent)
				.onErrorResume( error ->  reactiveLoggerUtils.logSevereAction( this.getClass().getSimpleName() + "]:[" +  GlobalConstants.MSG_FLOW_ERROR + "]:[" + error.toString() ) )
				.then();
		
	}

	private Mono<ConsumerRecord<String, Event>> validateTopic(ConsumerRecord<String, Event> consumer ){
		
		return Mono.justOrEmpty(consumer.topic())
				.filter( topic -> !topic.isEmpty() )
				.switchIfEmpty(Mono.error(new ValidationException(Map.of(this.getClass().getSimpleName(), GlobalConstants.EX_VALIDATION_DESC))))
				.thenReturn(consumer);				
				
	}
	
	private Mono<ConsumerRecord<String, Event>> validateValue(ConsumerRecord<String, Event> consumer ){
		
		return Mono.justOrEmpty(consumer.value())
				.switchIfEmpty(Mono.error(new ValidationException(Map.of(this.getClass().getSimpleName(), GlobalConstants.EX_VALIDATION_DESC))))
				.thenReturn(consumer);				
				
	}
	
    private Mono<Void> processEvent(ConsumerRecord<String, Event> consumer ){
    	
    	return Mono.justOrEmpty(consumer.topic())
    	.map(actionsMap::get)
		.switchIfEmpty(Mono.error(new IllegalArgumentException(this.getClass().getSimpleName() + GlobalConstants.MSG_HANDLER_NONE)))
		.flatMap( function -> function.apply(consumer.value()))
    	.then();
    	
    }

	private <T> Mono<T> logFlowStep(Mono<T> reactiveFlow){
		
		return reactiveFlow		
				.flatMap( data -> reactiveLoggerUtils.logInfoAction( this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_PROCESS, data.toString() ).thenReturn(data) );	
			
	}
	  
}

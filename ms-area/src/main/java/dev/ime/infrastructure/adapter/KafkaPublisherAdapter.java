package dev.ime.infrastructure.adapter;


import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.ime.application.exception.PublishEventException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.outbound.PublisherPort;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

@Service
public class KafkaPublisherAdapter implements PublisherPort{

	private final ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaTemplate;
	private final ReactiveLoggerUtils reactiveLoggerUtils;
	
	public KafkaPublisherAdapter(ReactiveLoggerUtils reactiveLoggerUtils, ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaTemplate) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.reactiveKafkaTemplate = reactiveKafkaTemplate;
	}

	@Transactional("kafkaTransactionManager")
    public Mono<Void> publishEvent(Event event) {
    	
		return Mono.justOrEmpty(event)
				.switchIfEmpty(Mono.error( new PublishEventException(Map.of(GlobalConstants.MSG_PUBLISH_FAIL, GlobalConstants.MSG_NODATA ))))						
		        .doOnNext(item -> this.logInfo(GlobalConstants.MSG_PUBLISH_EVENT, item.toString()))
		        .map( eventItem -> SenderRecord.create(new ProducerRecord<String, Object>(event.getEventType(), event), null))				
				.flatMap(reactiveKafkaTemplate::send)
				.doOnSuccess(this::handleSuccess)
	            .onErrorMap( ex -> new PublishEventException(Map.of(GlobalConstants.MSG_PUBLISH_FAIL, ex.getMessage())))
		    	.doOnError(this::handleFailure)
				.doFinally(signalType -> this.logInfo(GlobalConstants.MSG_PUBLISH_END, signalType.toString()))
	            .then();
		
    }
	
    private void handleSuccess(SenderResult<Object> result) {
    	
    	this.logInfo(GlobalConstants.MSG_PUBLISH_OK, result.recordMetadata().topic());

    }

	private void handleFailure(Throwable ex) {
		
	    this.logInfo(GlobalConstants.MSG_PUBLISH_FAIL, ex.getMessage());
   
	}
	
	private void logInfo(String action, String extraInfo) {
		
    	reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), action, extraInfo).subscribe();

	}

}

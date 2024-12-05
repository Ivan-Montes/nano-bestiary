package dev.ime.domain.ports.inbound;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import dev.ime.domain.event.Event;
import reactor.core.publisher.Mono;

public interface SubscriberPort {

	Mono<Void> onMessage(ConsumerRecord<String, Event> consumerRecord);

}

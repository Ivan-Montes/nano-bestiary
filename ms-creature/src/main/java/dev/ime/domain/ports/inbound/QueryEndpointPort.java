package dev.ime.domain.ports.inbound;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public interface QueryEndpointPort {

	Mono<ServerResponse>getAll(ServerRequest serverRequest);
	Mono<ServerResponse>getById(ServerRequest serverRequest);
	
}

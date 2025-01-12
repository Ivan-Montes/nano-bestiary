package dev.ime.application.handlers;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.GetByIdCreatureQuery;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdCreatureQueryHandler implements QueryHandler<Mono<Creature>>{

	private final ReadRepositoryPort<Creature> readRepositoryPort;

	public GetByIdCreatureQueryHandler(ReadRepositoryPort<Creature> readRepositoryPort) {
		super();
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Creature> handle(Query query) {
		
		return Mono.just(query)
		.cast(GetByIdCreatureQuery.class)
		.map(GetByIdCreatureQuery::creatureId)
		.flatMap(readRepositoryPort::findById);
		
	}
	
}

package dev.ime.application.handlers;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.GetAllCreatureQuery;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GetAllCreatureQueryHandler implements QueryHandler<Flux<Creature>>{

	private final ReadRepositoryPort<Creature> readRepositoryPort;

	public GetAllCreatureQueryHandler(ReadRepositoryPort<Creature> readRepositoryPort) {
		super();
		this.readRepositoryPort = readRepositoryPort;
	}
	
	@Override
	public Flux<Creature> handle(Query query) {

		return Mono.justOrEmpty(query)
				.cast(GetAllCreatureQuery.class)
				.map(GetAllCreatureQuery::pageable)
				.flatMapMany(readRepositoryPort::findAll);
		
	}
	
}

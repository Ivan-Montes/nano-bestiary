package dev.ime.application.handlers;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.GetAllAreaQuery;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GetAllAreaQueryHandler implements QueryHandler<Flux<Area>> {

	private final ReadRepositoryPort<Area> readRepositoryPort;

	public GetAllAreaQueryHandler(ReadRepositoryPort<Area> readRepositoryPort) {
		super();
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Flux<Area> handle(Query query) {
		
		return Mono.justOrEmpty(query)
				.cast(GetAllAreaQuery.class)
				.map(GetAllAreaQuery::pageable)
				.flatMapMany(readRepositoryPort::findAll);
		
	}
	
}

package dev.ime.application.handlers;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.GetByIdAreaQuery;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdAreaQueryHandler  implements QueryHandler<Mono<Area>> {

	private final ReadRepositoryPort<Area> readRepositoryPort;

	public GetByIdAreaQueryHandler(ReadRepositoryPort<Area> readRepositoryPort) {
		super();
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Area> handle(Query query) {
		
		return Mono.justOrEmpty(query)
				.cast(GetByIdAreaQuery.class)
				.map(GetByIdAreaQuery::areaId)
				.flatMap(readRepositoryPort::findById);		
		
	}
	
}

package dev.ime.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.dto.CreatureDto;
import dev.ime.application.usecases.GetAllCreatureQuery;
import dev.ime.application.usecases.GetByIdCreatureQuery;
import dev.ime.config.CreatureMapper;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QueryService implements QueryServicePort<CreatureDto>{

	private final QueryDispatcher queryDispatcher;
	private final CreatureMapper mapper;
	
	public QueryService(QueryDispatcher queryDispatcher, CreatureMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}
	
	@Override
	public Flux<CreatureDto> getAll(Pageable pageable) {		

	    return Mono.just(new GetAllCreatureQuery(pageable))
	            .flatMapMany(this::processGetAllQuery);
	
	}	

	private Flux<CreatureDto> processGetAllQuery(GetAllCreatureQuery query) {
		
		return Mono.fromSupplier( () -> {
					QueryHandler<Flux<Creature>> handler = queryDispatcher.getQueryHandler(query);
					return handler;
				})
				.flatMapMany( handler -> handler.handle(query))
				.map(mapper::fromDomainToDto);
	
	}

	@Override
	public Mono<CreatureDto> getById(UUID id) {

		return Mono.just(new GetByIdCreatureQuery(id))
	            .flatMap(this::processGetById);
		
	}	
	
	private Mono<CreatureDto> processGetById(GetByIdCreatureQuery query){
		
		return Mono.fromSupplier( () -> {
			QueryHandler<Mono<Creature>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		})
		.flatMap( handler -> handler.handle(query))
		.map(mapper::fromDomainToDto);
		
	}
	
}

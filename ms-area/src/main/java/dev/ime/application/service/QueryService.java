package dev.ime.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.dto.AreaDto;
import dev.ime.application.usecases.GetAllAreaQuery;
import dev.ime.application.usecases.GetByIdAreaQuery;
import dev.ime.config.AreaMapper;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QueryService implements QueryServicePort<AreaDto>{

	private final QueryDispatcher queryDispatcher;
	private final AreaMapper mapper;
	
	public QueryService(QueryDispatcher queryDispatcher, AreaMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Flux<AreaDto> getAll(Pageable pageable) {
		
	    return Mono.just(new GetAllAreaQuery(pageable))
	            .flatMapMany(this::processGetAllQuery);
	    
	}

	private Flux<AreaDto> processGetAllQuery(GetAllAreaQuery query) {
		
		return Mono.fromSupplier( () -> {
					QueryHandler<Flux<Area>> handler = queryDispatcher.getQueryHandler(query);
					return handler;
				})
				.flatMapMany( handler -> handler.handle(query))
				.map(mapper::fromDomainToDto);
	
	}

	@Override
	public Mono<AreaDto> getById(UUID id) {
		
		return Mono.just(new GetByIdAreaQuery(id))
	            .flatMap(this::processGetById);		
		
	}	
	
	private Mono<AreaDto> processGetById(GetByIdAreaQuery query){
		
		return Mono.fromSupplier( () -> {
			QueryHandler<Mono<Area>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		})
		.flatMap( handler -> handler.handle(query))
		.map(mapper::fromDomainToDto);
		
	}
	
}

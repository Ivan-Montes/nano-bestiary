package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handlers.GetAllAreaQueryHandler;
import dev.ime.application.handlers.GetByIdAreaQueryHandler;
import dev.ime.application.usecases.GetAllAreaQuery;
import dev.ime.application.usecases.GetByIdAreaQuery;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class QueryDispatcher {

	private final Map<Class<? extends Query>, QueryHandler<?>> queryHandlers = new HashMap<>();

	public QueryDispatcher(GetAllAreaQueryHandler getAllQueryHandler, GetByIdAreaQueryHandler getByIdQueryHandler) {
		super();
		queryHandlers.put(GetAllAreaQuery.class, getAllQueryHandler);
		queryHandlers.put(GetByIdAreaQuery.class, getByIdQueryHandler);		
	}

	public <U> QueryHandler<U> getQueryHandler(Query query){

		@SuppressWarnings("unchecked")
		Optional<QueryHandler<U>> optHandler = Optional.ofNullable((QueryHandler<U>)queryHandlers.get(query.getClass()));
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + query.getClass().getName()));	

	}

}

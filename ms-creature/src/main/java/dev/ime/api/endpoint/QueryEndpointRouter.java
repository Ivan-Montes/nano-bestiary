package dev.ime.api.endpoint;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.config.UriConfigProperties;

@Configuration
public class QueryEndpointRouter {

	private final UriConfigProperties uriConfigProperties;

	public QueryEndpointRouter(UriConfigProperties uriConfigProperties) {
		this.uriConfigProperties = uriConfigProperties;
	}

	@RouterOperations({
		@RouterOperation(
				path = "/api/v1/creatures", 
				beanClass = QueryEndpointHandler.class, 
				beanMethod = "getAll",
		        method = RequestMethod.GET,
		        produces = MediaType.APPLICATION_JSON_VALUE),
	    @RouterOperation(
	    		path = "/api/v1/creatures/{id}", 
	    		beanClass = QueryEndpointHandler.class, 
	    		beanMethod = "getById",
	            method = RequestMethod.GET,
	            produces = MediaType.APPLICATION_JSON_VALUE
	    		)
		})
	@Bean
	RouterFunction<ServerResponse> queryEndpointRoutes(QueryEndpointHandler queryEndpointHandler){
		
		return RouterFunctions.nest(RequestPredicates.path(uriConfigProperties.getEndpointUri()),
				RouterFunctions.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON),
				RouterFunctions.route(RequestPredicates.GET(""), queryEndpointHandler::getAll)
							.andRoute(RequestPredicates.GET("/{id}"), queryEndpointHandler::getById)
				));
	}
	
}

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
public class CommandEndpointRouter {

	private final UriConfigProperties uriConfigProperties;
	
	public CommandEndpointRouter(UriConfigProperties uriConfigProperties) {
		this.uriConfigProperties = uriConfigProperties;
	}
	
	@RouterOperations({
		@RouterOperation(
				path = "/api/v1/areas", 
				beanClass = CommandEndpointHandler.class, 
				beanMethod = "create",
		        method = RequestMethod.POST,
		        produces = MediaType.APPLICATION_JSON_VALUE),
	    @RouterOperation(
	    		path = "/api/v1/areas/{id}", 
	    		beanClass = CommandEndpointHandler.class, 
	    		beanMethod = "update",
	            method = RequestMethod.PUT,
	            produces = MediaType.APPLICATION_JSON_VALUE
	    		),
	    @RouterOperation(
	    		path = "/api/v1/areas/{id}", 
	    		beanClass = CommandEndpointHandler.class, 
	    		beanMethod = "deleteById",
	            method = RequestMethod.DELETE,
	            produces = MediaType.APPLICATION_JSON_VALUE
	    		)
		})
	@Bean
	RouterFunction<ServerResponse> commandEndpointRoutes(CommandEndpointHandler commandEndpointHandler) {
		
		return RouterFunctions.nest(RequestPredicates.path(uriConfigProperties.getEndpointUri()),
			   RouterFunctions.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON),
			   RouterFunctions.route(RequestPredicates.POST(""), commandEndpointHandler::create)
   						   .andRoute(RequestPredicates.PUT("/{id}"), commandEndpointHandler::update)
   						   .andRoute(RequestPredicates.DELETE("/{id}"), commandEndpointHandler::deleteById)   						   
					   ));
	}
	
}

package dev.ime.api.endpoint;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.CreatureDto;
import dev.ime.application.dto.PaginationDto;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.application.utils.SortingValidationUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.ports.inbound.QueryEndpointPort;
import dev.ime.domain.ports.inbound.QueryServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Component
@Tag(name = "Creatures", description = "API for managing Creatures")
public class QueryEndpointHandler implements QueryEndpointPort{

	private final QueryServicePort<CreatureDto> queryService;
	private final DtoValidator dtoValidator;
	private final SortingValidationUtils sortingValidator;
	private final ErrorHandler errorHandler;
	
	public QueryEndpointHandler(QueryServicePort<CreatureDto> queryService, DtoValidator dtoValidator,
			SortingValidationUtils sortingValidator, ErrorHandler errorHandler) {
		super();
		this.queryService = queryService;
		this.dtoValidator = dtoValidator;
		this.sortingValidator = sortingValidator;
		this.errorHandler = errorHandler;
	}

	@Operation(
            summary = "Get all Creatures",
            description = "Returns a list of all available Creatures"
        )
    @ApiResponse(
        responseCode = "200", 
        description = "List of Creatures found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreatureDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
		  
    	return createPaginationDto(serverRequest)
		.flatMap(dtoValidator::validateDto)
    	.flatMap(this::createPageable)
    	.flatMapMany(queryService::getAll)
        .collectList()
        .flatMap(dtos -> ServerResponse.ok().bodyValue(dtos))
        .switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
            serverRequest.path(), GlobalConstants.MSG_NODATA
        ))))	        
	    .onErrorResume(errorHandler::handleException);
	    
	}

    private Mono<PaginationDto> createPaginationDto(ServerRequest serverRequest) {
    	
    	return Mono.fromCallable( () -> {
    		
    		Integer page = serverRequest.queryParam(GlobalConstants.PS_PAGE).map(Integer::parseInt).orElse(0);
        	Integer size = serverRequest.queryParam(GlobalConstants.PS_SIZE).map(Integer::parseInt).orElse(100);
            String sortBy = serverRequest.queryParam(GlobalConstants.PS_BY)
                    .filter( sortField -> sortingValidator.isValidSortField(CreatureDto.class, sortField))
                    .orElseGet( () -> sortingValidator.getDefaultSortField(CreatureDto.class));
            String sortDir = serverRequest.queryParam(GlobalConstants.PS_DIR)
            		.map(String::toUpperCase)
            		.filter( sorting -> sorting.equals(GlobalConstants.PS_A) || sorting.equals(GlobalConstants.PS_D))
            		.orElse(GlobalConstants.PS_A);
            
            return new PaginationDto(page, size, sortBy, sortDir);
            
    	}).onErrorMap( e -> new IllegalArgumentException(GlobalConstants.MSG_PAGED_FAIL, e));    	
        
    }

    private Mono<PageRequest> createPageable(PaginationDto paginationDto) {

    	return Mono.fromCallable( () -> {
    		
    		Sort sort = Sort.by(Sort.Direction.fromString(paginationDto.sortDir()), paginationDto.sortBy());
            return PageRequest.of(paginationDto.page(), paginationDto.size(), sort);            
            
    	}).onErrorMap( e -> new IllegalArgumentException(GlobalConstants.MSG_PAGED_FAIL, e));  
        
    }

    @Operation(
            summary = "Get Creatures by ID",
            description = "Returns a specific Creatures based on its ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Creatures ID", schema = @Schema(type = "string", format = "uuid"))
                }
    		)
    @ApiResponse(
        responseCode = "200", 
        description = "Creatures found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreatureDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> getById(ServerRequest serverRequest) {
      	
    		return Mono.defer( () -> {
    			
    			try {
    				
    				UUID id = UUID.fromString( serverRequest.pathVariable("id") );
    				
    				return queryService
    						.getById(id)
    						.flatMap( dto -> ServerResponse.ok().bodyValue(dto))
    						.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
    				                serverRequest.path(), GlobalConstants.MSG_NODATA
    				            ))))
    						.onErrorResume(errorHandler::handleException);
    				
    			} catch (IllegalArgumentException error) {
    					
    	            return errorHandler.handleException(new InvalidUUIDException(Map.of(GlobalConstants.CREATURE_ID, GlobalConstants.MSG_NODATA)));
    	            
    	        }			
    		});
	}
    
}

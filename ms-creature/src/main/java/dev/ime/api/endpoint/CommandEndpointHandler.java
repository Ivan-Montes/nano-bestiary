package dev.ime.api.endpoint;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.CreatureDto;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.ports.inbound.CommandEndpointPort;
import dev.ime.domain.ports.inbound.CommandServicePort;
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
public class CommandEndpointHandler implements CommandEndpointPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
	private final CommandServicePort<CreatureDto> commandService;
	private final DtoValidator dtoValidator;
	private final ErrorHandler errorHandler;

	public CommandEndpointHandler(ReactiveLoggerUtils reactiveLoggerUtils, CommandServicePort<CreatureDto> commandService, DtoValidator dtoValidator,
			ErrorHandler errorHandler) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.commandService = commandService;
		this.dtoValidator = dtoValidator;
		this.errorHandler = errorHandler;
	}

	@Operation(
            summary = "Create Creature",
            description = "Returns created Creature",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Creature object that needs to be created",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CreatureDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Created Creature",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreatureDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> create(ServerRequest serverRequest) {
		
		return serverRequest.bodyToMono(CreatureDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(commandService::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))				
				.transform(this::addLogginOptions)
				.onErrorResume(errorHandler::handleException);
		
	}

	@Operation(
            summary = "Update Creature",
            description = "Returns updated Creature",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Creature ID", schema = @Schema(type = "string", format = "uuid"))
                },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Creature object that needs to be updated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreatureDto.class)
                )
            )        
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Updated Creature",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreatureDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> update(ServerRequest serverRequest) {

		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
				.map(UUID::fromString)
				.onErrorMap(IllegalArgumentException.class, error -> new InvalidUUIDException(Map.of(GlobalConstants.CREATURE_ID, GlobalConstants.MSG_NODATA)))
				.flatMap( id -> serverRequest.bodyToMono(CreatureDto.class)
						.flatMap(dtoValidator::validateDto)
						.flatMap( dto -> commandService.update(id, dto))
						)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))				
				.transform(this::addLogginOptions)
				.onErrorResume(errorHandler::handleException);
		
	}

	@Operation(
            summary = "Delete Creature",
            description = "Returns deleted Creature",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Creature ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Deleted Creature",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreatureDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {

		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
		.map(UUID::fromString)
		.flatMap(commandService::deleteById)
		.flatMap( obj -> ServerResponse.ok().bodyValue(obj))
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
                serverRequest.path(), GlobalConstants.MSG_NODATA
            ))))				
		.transform(this::addLogginOptions)
		.onErrorResume(errorHandler::handleException);
		
	}

	private <T> Mono<T> addLogginOptions(Mono<T> reactiveFlow){
		
		return reactiveFlow
				.doOnSubscribe( subscribed -> this.logInfo( GlobalConstants.MSG_FLOW_SUBS, subscribed.toString()) )
				.doOnSuccess( success -> this.logInfo( GlobalConstants.MSG_FLOW_OK, createExtraInfo(success) ))
	            .doOnCancel( () -> this.logInfo( GlobalConstants.MSG_FLOW_CANCEL, GlobalConstants.MSG_NODATA) )
	            .doOnError( error -> this.logInfo( GlobalConstants.MSG_FLOW_ERROR, error.toString()) )
		        .doFinally( signal -> this.logInfo( GlobalConstants.MSG_FLOW_RESULT, signal.toString()) );		
			
	}
	
	private void logInfo(String action, String extraInfo) {
		
    	reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), action, extraInfo).subscribe();

	}
	
	private <T> String createExtraInfo(T response) {
		
		return response instanceof Number? GlobalConstants.MSG_MODLINES + response.toString():response.toString();
				
	}
	
}

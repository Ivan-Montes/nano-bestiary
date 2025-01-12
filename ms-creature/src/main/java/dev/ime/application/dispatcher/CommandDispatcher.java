package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handlers.CreateCreatureCommandHandler;
import dev.ime.application.handlers.DeleteCreatureCommandHandler;
import dev.ime.application.handlers.UpdateCreatureCommandHandler;
import dev.ime.application.usecases.CreateCreatureCommand;
import dev.ime.application.usecases.DeleteCreatureCommand;
import dev.ime.application.usecases.UpdateCreatureCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@Component
public class CommandDispatcher {
	
	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public CommandDispatcher(CreateCreatureCommandHandler createCommandHandler, UpdateCreatureCommandHandler updateCommandHandler, DeleteCreatureCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateCreatureCommand.class, createCommandHandler);
		commandHandlers.put(UpdateCreatureCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteCreatureCommand.class, deleteCommandHandler);
	}

	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + command.getClass().getName()));	
		
	}
	
}

package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handlers.CreateAreaCommandHandler;
import dev.ime.application.handlers.DeleteAreaCommandHandler;
import dev.ime.application.handlers.UpdateAreaCommandHandler;
import dev.ime.application.usecases.CreateAreaCommand;
import dev.ime.application.usecases.DeleteAreaCommand;
import dev.ime.application.usecases.UpdateAreaCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@Component
public class CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public CommandDispatcher(CreateAreaCommandHandler createCommandHandler, UpdateAreaCommandHandler updateCommandHandler, DeleteAreaCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateAreaCommand.class, createCommandHandler);
		commandHandlers.put(UpdateAreaCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteAreaCommand.class, deleteCommandHandler);
	}
	
	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + command.getClass().getName()));	
		
	}
	
}

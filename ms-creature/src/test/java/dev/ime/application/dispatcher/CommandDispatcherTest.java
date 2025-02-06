package dev.ime.application.dispatcher;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.handlers.CreateCreatureCommandHandler;
import dev.ime.application.handlers.DeleteCreatureCommandHandler;
import dev.ime.application.handlers.UpdateCreatureCommandHandler;
import dev.ime.application.usecases.DeleteCreatureCommand;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;

@ExtendWith(MockitoExtension.class)
class CommandDispatcherTest {

	@Mock
	private CreateCreatureCommandHandler createCommandHandler;

	@Mock
	private UpdateCreatureCommandHandler updateCommandHandler;

	@Mock
	private DeleteCreatureCommandHandler deleteCommandHandler;
	
	@InjectMocks
	private CommandDispatcher commandDispatcher;
	
	private class CommandTest implements Command{}
	
	@Test
	void getCommandHandler_shouldReturnHandler() {
		
		DeleteCreatureCommand command = new DeleteCreatureCommand(UUID.randomUUID());
		
		CommandHandler handler = commandDispatcher.getCommandHandler(command);
		
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(handler).isNotNull()
				);
		
	}

	@Test
	void getCommandHandler_WithUnknownCommand_ThrowError() {
		
		CommandTest commandTest = new CommandTest();
		
		Exception ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> commandDispatcher.getCommandHandler(commandTest));
		
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(ex).isNotNull(),
				() -> Assertions.assertThat(ex.getClass()).isEqualTo(IllegalArgumentException.class)
				);		
		
	}	

}

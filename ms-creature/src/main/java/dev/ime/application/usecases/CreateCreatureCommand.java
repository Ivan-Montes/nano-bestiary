package dev.ime.application.usecases;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record CreateCreatureCommand(
		UUID creatureId,
		String creatureName,
		String creatureDescription,
		UUID areaId
		) implements Command {

}

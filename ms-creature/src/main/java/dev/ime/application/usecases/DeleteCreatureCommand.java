package dev.ime.application.usecases;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record DeleteCreatureCommand(
		UUID creatureId
		) implements Command {

}

package dev.ime.application.usecases;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record UpdateAreaCommand(
		UUID areaId,
		String areaName
		) implements Command {

}

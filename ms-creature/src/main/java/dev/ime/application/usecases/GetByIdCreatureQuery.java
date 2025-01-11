package dev.ime.application.usecases;

import java.util.UUID;

import dev.ime.domain.query.Query;

public record GetByIdCreatureQuery(
		UUID creatureId
		) implements Query {

}

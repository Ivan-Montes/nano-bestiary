package dev.ime.application.usecases;

import org.springframework.data.domain.Pageable;

import dev.ime.domain.query.Query;

public record GetAllCreatureQuery(Pageable pageable) implements Query{

}

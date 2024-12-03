package dev.ime.domain.query;

public interface QueryHandler<T> {

	T handle(Query query);
}

package dev.ime.application.dispatcher;


import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.handlers.GetAllAreaQueryHandler;
import dev.ime.application.handlers.GetByIdAreaQueryHandler;
import dev.ime.application.usecases.GetByIdAreaQuery;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@ExtendWith(MockitoExtension.class)
class QueryDispatcherTest {

	@Mock
	private GetAllAreaQueryHandler getAllQueryHandler;
	
	@Mock
	private GetByIdAreaQueryHandler getByIdQueryHandler;

	@InjectMocks
	private QueryDispatcher queryDispatcher;
	
	private class QueryTest implements Query{}
	
	@Test
	void getQueryHandler_shouldReturnHandler() {
		
		GetByIdAreaQuery query = new GetByIdAreaQuery(UUID.randomUUID());	
		
		QueryHandler<Object> handler = queryDispatcher.getQueryHandler(query);
		
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(handler).isNotNull()
				);
		
	}

	@Test
	void getQueryHandler_shouldThrowException() {
		
		QueryTest queryTest = new QueryTest();
		
		Exception ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> queryDispatcher.getQueryHandler(queryTest));
		
		org.junit.jupiter.api.Assertions.assertAll(
				() -> Assertions.assertThat(ex).isNotNull(),
				() -> Assertions.assertThat(ex.getClass()).isEqualTo(IllegalArgumentException.class)
				);
	}

}

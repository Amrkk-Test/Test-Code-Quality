package com.triplelift.test_code_quality_tools_api.exceptions;

import graphql.execution.DataFetcherExceptionHandlerParameters;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import graphql.language.SourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class CustomDataFetchingExceptionHandlerTest {
	private final CustomDataFetchingExceptionHandler customDataFetchingExceptionHandler = new CustomDataFetchingExceptionHandler();
	private final DataFetcherExceptionHandlerParameters dataFetcherExceptionHandlerParametersMock = Mockito
			.mock(DataFetcherExceptionHandlerParameters.class);

	@BeforeEach
	public void setup() {

		ResultPath path = ResultPath.parse("/home/user");
		Mockito.when(dataFetcherExceptionHandlerParametersMock.getPath()).thenReturn(path);
		Mockito.when(dataFetcherExceptionHandlerParametersMock.getSourceLocation())
				.thenReturn(Mockito.mock(SourceLocation.class));
	}

	@Test
	public void testRunTimeException() {

		String message = "RunTime Exception";

		RuntimeException runtimeExceptionMock = Mockito.mock(RuntimeException.class);
		Mockito.when(runtimeExceptionMock.getMessage()).thenReturn(message);
		Mockito.when(dataFetcherExceptionHandlerParametersMock.getException()).thenReturn(runtimeExceptionMock);

		CompletableFuture<DataFetcherExceptionHandlerResult> result = customDataFetchingExceptionHandler
				.handleException(dataFetcherExceptionHandlerParametersMock);
		result.whenComplete((res, ex) -> assertNotNull(res));
	}

	@Test
	public void testWithOtherExceptions() {

		InterruptedException interruptedExceptionMock = Mockito.mock(InterruptedException.class);
		Mockito.when(dataFetcherExceptionHandlerParametersMock.getException()).thenReturn(interruptedExceptionMock);

		CompletableFuture<DataFetcherExceptionHandlerResult> result = customDataFetchingExceptionHandler
				.handleException(dataFetcherExceptionHandlerParametersMock);
		result.whenComplete((res, ex) -> assertNotNull(res));
	}
}
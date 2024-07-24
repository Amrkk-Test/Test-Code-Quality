package com.triplelift.test_code_quality_tools_api.exceptions;

import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CustomDataFetchingExceptionHandler implements DataFetcherExceptionHandler {

	@Override
	public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
			DataFetcherExceptionHandlerParameters handlerParameters) {

		if (handlerParameters.getException() instanceof RuntimeException) {
			GraphQLError graphqlError = TypedGraphQLError.newInternalErrorBuilder()
					.message(handlerParameters.getException().getMessage()).path(handlerParameters.getPath()).build();
			DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult().error(graphqlError)
					.build();
			return CompletableFuture.completedFuture(result);
		}

		SimpleDataFetcherExceptionHandler defaultHandler = new SimpleDataFetcherExceptionHandler();
		defaultHandler = new SimpleDataFetcherExceptionHandler();
		return defaultHandler.handleException(handlerParameters);
	}

}

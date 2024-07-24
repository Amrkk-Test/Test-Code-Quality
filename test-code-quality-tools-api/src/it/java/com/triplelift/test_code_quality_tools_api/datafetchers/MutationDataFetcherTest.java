package com.triplelift.test_code_quality_tools_api.datafetchers;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import com.triplelift.gen.client.CreateGreetingGraphQLQuery;
import com.triplelift.gen.client.CreateGreetingProjectionRoot;
import com.triplelift.gen.client.DeleteGreetingGraphQLQuery;
import com.triplelift.gen.client.GreetingProjectionRoot;
import com.triplelift.gen.client.UpdateGreetingGraphQLQuery;
import com.triplelift.gen.client.UpdateGreetingProjectionRoot;
import com.triplelift.gen.types.GreetingInput;
import com.triplelift.test_code_quality_tools_api.Application;
import graphql.ExecutionResult;
import graphql.schema.Coercing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static graphql.scalars.java.JavaPrimitives.GraphQLBigInteger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = { DgsAutoConfiguration.class,
		Application.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MutationDataFetcherTest {
	@Autowired
	DgsQueryExecutor dgsQueryExecutor;

	private static final Map<Class<?>, Coercing<?, ?>> SCALARS = Map.of(java.math.BigInteger.class,
			GraphQLBigInteger.getCoercing());

	@WithMockUser(username = "testUser", authorities = { "SCOPE_create:greetings" })
	@Test
	void createGreetingShouldReturnSuccessfulResponse() {
		CreateGreetingGraphQLQuery gqlQuery = new CreateGreetingGraphQLQuery.Builder().message("Hello!").build();
		CreateGreetingProjectionRoot fields = new CreateGreetingProjectionRoot().message().id();
		GraphQLQueryRequest queryRequest = new GraphQLQueryRequest(gqlQuery, fields);

		ExecutionResult response = dgsQueryExecutor.execute(queryRequest.serialize());

		assertThat(response.getErrors()).isEmpty();
		assertNotNull(response.getData());
	}

	@WithMockUser(username = "testUser", authorities = { "SCOPE_update:greetings" })
	@Test
	void updateGreetingShouldReturnSuccessfulResponse() {
		BigInteger id = BigInteger.valueOf(123);
		String message = "Hello again!";
		GreetingInput input = GreetingInput.newBuilder().id(id).greeting(message).build();
		UpdateGreetingGraphQLQuery gqlQuery = new UpdateGreetingGraphQLQuery.Builder().greetingInput(input).build();
		UpdateGreetingProjectionRoot fields = new UpdateGreetingProjectionRoot().message().id();
		GraphQLQueryRequest queryRequest = new GraphQLQueryRequest(gqlQuery, fields, SCALARS);

		HashMap<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(queryRequest.serialize(),
				"data.updateGreeting");

		assertThat(result.get("id").toString()).isEqualTo(id.toString());
		assertThat(result.get("message")).isEqualTo(message);
	}

	@WithMockUser(username = "testUser", authorities = { "SCOPE_delete:greetings" })
	@Test
	void deleteGreetingShouldReturnSuccessfulResponse() {
		BigInteger id = BigInteger.valueOf(321);
		DeleteGreetingGraphQLQuery gqlQuery = new DeleteGreetingGraphQLQuery.Builder().id(id).build();
		GreetingProjectionRoot greetingProjectionRoot = new GreetingProjectionRoot();
		GraphQLQueryRequest queryRequest = new GraphQLQueryRequest(gqlQuery, greetingProjectionRoot, SCALARS);
		ExecutionResult result = dgsQueryExecutor.execute(queryRequest.serialize());

		assertThat(result.getErrors()).isEmpty();
	}

}

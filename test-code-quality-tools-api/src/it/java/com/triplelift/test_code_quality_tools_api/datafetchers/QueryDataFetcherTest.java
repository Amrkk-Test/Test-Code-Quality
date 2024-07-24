package com.triplelift.test_code_quality_tools_api.datafetchers;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import com.triplelift.gen.client.GreetingGraphQLQuery;
import com.triplelift.gen.client.GreetingProjectionRoot;
import com.triplelift.gen.client.GreetingsGraphQLQuery;
import com.triplelift.gen.client.GreetingsProjectionRoot;
import com.triplelift.gen.types.GreetingsFilter;
import com.triplelift.test_code_quality_tools_api.Application;
import graphql.ExecutionResult;
import graphql.schema.Coercing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static graphql.scalars.java.JavaPrimitives.GraphQLBigInteger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = { DgsAutoConfiguration.class,
		Application.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryDataFetcherTest {

	private static final Map<Class<?>, Coercing<?, ?>> SCALARS = Map.of(java.math.BigInteger.class,
			GraphQLBigInteger.getCoercing());

	@Autowired
	DgsQueryExecutor dgsQueryExecutor;

	@WithMockUser(username = "testUser", authorities = { "SCOPE_read:greetings" })
	@Test
	void GreetingQueryShouldReturnSuccessfulResponse() {
		BigInteger id = BigInteger.valueOf(123L); // from seed-greetings-data.sql
		GreetingGraphQLQuery gqlQuery = new GreetingGraphQLQuery.Builder().id(id).build();
		GreetingProjectionRoot fields = new GreetingProjectionRoot().message().id();
		GraphQLQueryRequest queryRequest = new GraphQLQueryRequest(gqlQuery, fields, SCALARS);

		ExecutionResult response = dgsQueryExecutor.execute(queryRequest.serialize());

		assertNotNull(response.getData());
		assertThat(response.getErrors()).isEmpty();
	}

	@WithMockUser(username = "testUser", authorities = { "SCOPE_read:greetings" })
	@Test
	void GreetingsQueryShouldReturnSuccessfulResponse() {
		BigInteger id = BigInteger.valueOf(123);
		GreetingsFilter filter = GreetingsFilter.newBuilder().ids(List.of(id)).build();
		GreetingsGraphQLQuery gqlQuery = new GreetingsGraphQLQuery.Builder().filter(filter).build();
		GreetingsProjectionRoot fields = new GreetingsProjectionRoot().id();
		GraphQLQueryRequest queryRequest = new GraphQLQueryRequest(gqlQuery, fields, SCALARS);

		BigInteger result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(queryRequest.serialize(),
				"data.greetings[0].id", BigInteger.class);

		assertThat(result).isEqualTo(id);
	}

	@WithMockUser(username = "testUser", authorities = { "SCOPE_read:greetings" })
	@Test
	void GreetingsQueryShouldReturnErrorsIfNoGreetingsWereFound() {
		List<BigInteger> ids = List.of(BigInteger.valueOf(789));
		GreetingsFilter filter = GreetingsFilter.newBuilder().ids(ids).build();
		GreetingsGraphQLQuery gqlQuery = new GreetingsGraphQLQuery.Builder().filter(filter).build();
		GreetingsProjectionRoot fields = new GreetingsProjectionRoot().message().id();
		GraphQLQueryRequest queryRequest = new GraphQLQueryRequest(gqlQuery, fields, SCALARS);

		ExecutionResult response = dgsQueryExecutor.execute(queryRequest.serialize());

		assertThat(response.getErrors()).isNotEmpty();
	}

}

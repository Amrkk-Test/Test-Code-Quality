package com.triplelift.test_code_quality_tools_api.config;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;

import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;

@DgsComponent
public class GraphQLConfig {
	@DgsRuntimeWiring
	public RuntimeWiring.Builder addScalar(RuntimeWiring.Builder builder) {
		return builder.scalar(ExtendedScalars.Date).scalar(ExtendedScalars.DateTime)
				.scalar(ExtendedScalars.GraphQLBigDecimal).scalar(ExtendedScalars.GraphQLBigInteger)
				.scalar(ExtendedScalars.GraphQLByte).scalar(ExtendedScalars.GraphQLChar)
				.scalar(ExtendedScalars.GraphQLShort).scalar(ExtendedScalars.GraphQLLong);
	}
}

package com.triplelift.test_code_quality_tools_api.config.graphql;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.visibility.BlockedFields;
import graphql.schema.visibility.GraphqlFieldVisibility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.visibility.BlockedFields.newBlock;

/***
 * @implNote Class used to check graphql query for any blocked patterns. Note that this will not parse params in the
 *           query(ex. id)
 */
@Component
public class GraphQLQueryInputValidator implements GraphqlFieldVisibility {

	private final String env;
	private final static String PRODUCTION = "production";
	private final BlockedFields blockedFields;

	/**
	 * If in prod environment, block introspection hence the __
	 * 
	 * @param env
	 */
	public GraphQLQueryInputValidator(@Value("${env}") String env) {
		this.env = env;
		if (this.env.equals(PRODUCTION)) {
			this.blockedFields = newBlock().addPattern("__.*").build();
		} else {
			this.blockedFields = newBlock().build();
		}
	}

	@Override
	public List<GraphQLFieldDefinition> getFieldDefinitions(GraphQLFieldsContainer fieldsContainer) {
		return blockedFields.getFieldDefinitions(fieldsContainer);
	}

	@Override
	public GraphQLFieldDefinition getFieldDefinition(GraphQLFieldsContainer fieldsContainer, String fieldName) {
		return blockedFields.getFieldDefinition(fieldsContainer, fieldName);
	}
}

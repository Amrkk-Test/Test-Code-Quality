package com.triplelift.test_code_quality_tools_api.metrics;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Component
public class CustomMetricsInstrumentation extends TracingInstrumentation {

	private static final String QUERY_STATUS_COUNTER_METRIC_NAME = "graphql.counter.query";
	private static final String OPERATION_NAME_TAG = "operationName";
	private static final String UNKNOWN_OPERATION_NAME = "unknown";

	private MeterRegistry meterRegistry;

	public CustomMetricsInstrumentation(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@Override
	public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult,
			InstrumentationExecutionParameters parameters) {

		String status = CollectionUtils.isEmpty(executionResult.getErrors()) ? "success" : "error";
		String operation = parameters.getOperation() != null ? parameters.getOperation() : UNKNOWN_OPERATION_NAME;
		Collection<Tag> tags = Arrays.asList(Tag.of(OPERATION_NAME_TAG, operation));

		meterRegistry.counter(QUERY_STATUS_COUNTER_METRIC_NAME + "." + status, tags).increment();

		return super.instrumentExecutionResult(executionResult, parameters);
	}
}
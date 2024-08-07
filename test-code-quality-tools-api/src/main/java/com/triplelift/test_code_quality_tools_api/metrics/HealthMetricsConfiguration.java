package com.triplelift.test_code_quality_tools_api.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthMetricsConfiguration {

	/**
	 * All gauges are given a strong reference to prevent GC - otherwise Micrometer will start reporting nothing or NaN.
	 * Ref: Section 9.3 of https://micrometer.io/docs/concepts
	 */
	public HealthMetricsConfiguration(MeterRegistry registry, HealthEndpoint healthEndpoint) {
		Gauge.builder("actuator.health.app", new GaugeObject(healthEndpoint, ""), this::getStatusCode)
				.strongReference(true).register(registry);
		Gauge.builder("actuator.health.app", new GaugeObject(healthEndpoint, ""), this::getStatusCode)
				.strongReference(true).register(registry);
		Gauge.builder("actuator.health.app", new GaugeObject(healthEndpoint, ""), this::getStatusCode)
				.strongReference(true).register(registry);
		Gauge.builder("actuator.health.app", new GaugeObject(healthEndpoint, ""), this::getStatusCode)
				.strongReference(true).register(registry);
		Gauge.builder("actuator.health.diskspace", new GaugeObject(healthEndpoint, "diskSpace"), this::getStatusCode)
				.strongReference(true).register(registry);
		Gauge.builder("actuator.health.datasources", new GaugeObject(healthEndpoint, "db"), this::getStatusCode)
				.strongReference(true).register(registry);
		Gauge.builder("actuator.health.sqs", new GaugeObject(healthEndpoint, "SQSHealthCheck"), this::getStatusCode)
				.strongReference(true).register(registry);
	}

	private static class GaugeObject {
		HealthEndpoint endpoint;
		String path;

		GaugeObject(HealthEndpoint endpoint, String path) {
			this.endpoint = endpoint;
			this.path = path;
		}
	}

	private int getStatusCode(GaugeObject obj) {
		Status status;

		if (obj.path.isEmpty()) {
			status = obj.endpoint.health().getStatus();
		} else {
			status = obj.endpoint.healthForPath(obj.path).getStatus();
		}

		// Checks for all default, known statuses used by the Spring Boot health system.
		if (Status.UP.equals(status)) {
			return 3;
		}
		if (Status.OUT_OF_SERVICE.equals(status)) {
			return 2;
		}
		if (Status.DOWN.equals(status)) {
			return 1;
		}

		return 0;
	}

}

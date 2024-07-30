package com.triplelift.test_code_quality_tools_api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@EnableCaching
public class Application {
	//Testing new changes to original code
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * `ExtendedScalars.Date` resolves reporting query date inputs as `java.time.LocalDate` objects at runtime.
	 *
	 * However, the `LocalDate` class does not store or represent a time or time-zone. Instead, it parses dates
	 * according to the default system time.
	 *
	 * We set timezone here to parse query date inputs in UTC.
	 */
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		log.info("Set system timezone to UTC.");
	}

}

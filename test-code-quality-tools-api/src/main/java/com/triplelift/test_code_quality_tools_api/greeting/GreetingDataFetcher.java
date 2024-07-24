package com.triplelift.test_code_quality_tools_api.greeting;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

import com.netflix.graphql.dgs.InputArgument;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.DgsSubscription;
import com.triplelift.gen.types.*;

import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Flux;

@DgsComponent
public class GreetingDataFetcher {

	@Autowired
	private final GreetingService greetingService;

	@Autowired
	public GreetingDataFetcher(GreetingService greetingService) {
		this.greetingService = greetingService;
	}

	@PreAuthorize("hasAuthority('SCOPE_read:greetings')")
	@DgsQuery
	public Greeting greeting(BigInteger id) {
		return greetingService.getInMemoryGreeting(id);
	}

	@PreAuthorize("hasAuthority('SCOPE_read:greetings')")
	@DgsQuery
	public List<Greeting> greetings(@InputArgument GreetingsFilter filter) {
		return greetingService.getGreetings(filter);
	}

	@PreAuthorize("hasAuthority('SCOPE_create:greetings')")
	@DgsMutation
	public Greeting createGreeting(String message) {
		return greetingService.createGreeting(message);
	}

	@PreAuthorize("hasAuthority('SCOPE_update:greetings')")
	@DgsMutation
	public Greeting updateGreeting(GreetingInput greetingInput) {
		return greetingService.updateInMemoryGreeting(greetingInput);
	}

	@PreAuthorize("hasAuthority('SCOPE_delete:greetings')")
	@DgsMutation
	public String deleteGreeting(BigInteger id) {
		return greetingService.deleteInMemoryGreeting(id);
	}

	@PreAuthorize("hasAuthority('SCOPE_subscribe:greetings')")
	@DgsSubscription
	public Publisher<List<Greeting>> subscribeToGreetings() {
		return Flux.interval(Duration.ofSeconds(0), Duration.ofSeconds(1))
				.map(t -> greetingService.getGreetings(new GreetingsFilter()));
	}
}
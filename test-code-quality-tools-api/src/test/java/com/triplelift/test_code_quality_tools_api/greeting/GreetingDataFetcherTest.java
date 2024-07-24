package com.triplelift.test_code_quality_tools_api.greeting;

import com.triplelift.gen.types.GreetingInput;
import com.triplelift.gen.types.GreetingsFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.test.StepVerifier;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class GreetingDataFetcherTest {

	private GreetingService greetingServiceMock = Mockito.mock(GreetingService.class);
	private GreetingDataFetcher greetingDataFetcher = new GreetingDataFetcher(greetingServiceMock);

	@Test
	public void testGetInMemoryGreeting() {
		BigInteger greetingId = new BigInteger(String.valueOf(123));
		Greeting greetingResponseMessage = new Greeting();
		when(greetingServiceMock.getInMemoryGreeting(greetingId)).thenReturn(greetingResponseMessage);

		Greeting greetingResponse = greetingDataFetcher.greeting(greetingId);

		// Checking if same address space
		assertTrue(greetingResponse == greetingResponseMessage);
		verify(greetingServiceMock, times(1)).getInMemoryGreeting(greetingId);
	}

	@Test
	public void testGetAllGreetings() {
		List<Greeting> greetings = List.of();
		when(greetingServiceMock.getGreetings(new GreetingsFilter())).thenReturn(List.of());

		List<Greeting> greetingsResponse = greetingDataFetcher.greetings(new GreetingsFilter());

		// Checking if same address space
		assertTrue(greetingsResponse == greetings);
		verify(greetingServiceMock, times(1)).getGreetings(any());
	}

	@Test
	public void testGetGreeting() {
		BigInteger greetingId123 = new BigInteger(String.valueOf(123));
		BigInteger greetingId456 = new BigInteger(String.valueOf(456));
		BigInteger greetingId789 = new BigInteger(String.valueOf(789));
		List<BigInteger> greetingIds = List.of(greetingId123, greetingId456, greetingId789);
		GreetingsFilter greetingsFilter = new GreetingsFilter(greetingIds);
		List<Greeting> greetingResponseMessage = List.of(new Greeting(greetingId123, "M1"),
				new Greeting(greetingId456, "M2"), new Greeting(greetingId789, "M3"));
		when(greetingServiceMock.getGreetings(greetingsFilter)).thenReturn(greetingResponseMessage);

		List<Greeting> greetingsResponse = greetingDataFetcher.greetings(greetingsFilter);

		// Checking if same address space
		assertTrue(greetingsResponse == greetingResponseMessage);
		verify(greetingServiceMock, times(1)).getGreetings(greetingsFilter);
	}

	@Test
	public void testCreateInMemoryGreeting() {
		Greeting greetingResponseMessage = new Greeting();
		String greeting = "Hello";
		when(greetingServiceMock.createGreeting(greeting)).thenReturn(greetingResponseMessage);

		Greeting greetingResponse = greetingDataFetcher.createGreeting(greeting);

		// Checking if same address space
		assertTrue(greetingResponse == greetingResponseMessage);
		verify(greetingServiceMock, times(1)).createGreeting(greeting);
	}

	@Test
	public void testUpdateInMemoryGreeting() {
		Greeting greetingResponseMessage = new Greeting();
		GreetingInput greetingInputMock = Mockito.mock(GreetingInput.class);
		when(greetingServiceMock.updateInMemoryGreeting(greetingInputMock)).thenReturn(greetingResponseMessage);

		Greeting greetingResponse = greetingDataFetcher.updateGreeting(greetingInputMock);

		// Checking if same address space
		assertTrue(greetingResponse == greetingResponseMessage);
		verify(greetingServiceMock, times(1)).updateInMemoryGreeting(greetingInputMock);
	}

	@Test
	public void testDeleteInMemoryGreeting() {
		BigInteger greetingId = new BigInteger(String.valueOf(123));
		String deleteGreetingResponseMessage = "Deleted greeting with ID:" + greetingId;
		when(greetingServiceMock.deleteInMemoryGreeting(greetingId)).thenReturn(deleteGreetingResponseMessage);

		String greetingResponse = greetingDataFetcher.deleteGreeting(greetingId);

		// Checking if same address space
		assertTrue(greetingResponse == deleteGreetingResponseMessage);
		verify(greetingServiceMock, times(1)).deleteInMemoryGreeting(greetingId);
	}

	@Test
	public void testSubscribeToGreetings() {
		List<Greeting> greetingResponseMock = List.of();
		when(greetingServiceMock.getGreetings(new GreetingsFilter())).thenReturn(greetingResponseMock);
		StepVerifier.create(greetingDataFetcher.subscribeToGreetings())
				.expectNextMatches(response -> response == greetingResponseMock).thenCancel().verify();

		verify(greetingServiceMock, times(1)).getGreetings(any());
	}
}

package com.triplelift.test_code_quality_tools_api.greeting;

import com.triplelift.test_code_quality_tools_api.exceptions.GreetingCreationException;
import com.triplelift.test_code_quality_tools_api.exceptions.GreetingDeletionException;
import com.triplelift.test_code_quality_tools_api.exceptions.GreetingNotFoundException;
import com.triplelift.test_code_quality_tools_api.exceptions.GreetingNotUpdatedException;
import com.triplelift.gen.types.GreetingInput;
import com.triplelift.gen.types.GreetingsFilter;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;

public class GreetingServiceTest {

	private final GreetingRepository greetingRepositoryMock = Mockito.mock(GreetingRepository.class);
	private final GreetingService greetingService = new GreetingService(greetingRepositoryMock);

	Exception exception = new RuntimeException();

	@Test
	public void testGetInMemoryGreetingWhenEmptyGreetingThrowsException() {
		BigInteger greetingId = new BigInteger(String.valueOf(123));
		Mockito.when(greetingRepositoryMock.findById(greetingId)).thenReturn(Optional.empty());

		assertThrows(GreetingNotFoundException.class, () -> greetingService.getInMemoryGreeting(greetingId));
	}

	@Test
	public void testGetInMemoryGreetingWhenGreetingIsNotEmpty() {
		BigInteger greetingId = new BigInteger(String.valueOf(123));
		Greeting greeting = new Greeting();
		Optional<Greeting> optionalGreeting = Optional.of(greeting);

		Mockito.when(greetingRepositoryMock.findById(greetingId)).thenReturn(optionalGreeting);

		Greeting greetingResponseActual = greetingService.getInMemoryGreeting(greetingId);

		assertEquals(greeting, greetingResponseActual);
	}

	@Test
	public void testGetInMemoryGreetingWhenExceptionIsThrown() {
		BigInteger greetingId = new BigInteger(String.valueOf(123));
		Optional<Greeting> emptyGreeting = Optional.empty();
		Mockito.when(greetingRepositoryMock.findById(greetingId)).thenReturn(emptyGreeting);

		assertThrows(GreetingNotFoundException.class, () -> greetingService.getInMemoryGreeting(greetingId));
	}

	@Test
	public void testGetAllGreetings() {
		List<Greeting> greetingList = List.of(new Greeting());

		Mockito.when(greetingRepositoryMock.findAll()).thenReturn(greetingList);

		assertEquals(greetingList, greetingService.getGreetings(new GreetingsFilter()));
	}

	@Test
	public void testGetAllGreetingsExceptionThrown() {
		List<Greeting> greetingList = List.of();
		Mockito.when(greetingRepositoryMock.findAll()).thenReturn(greetingList);

		assertThrows(GreetingNotFoundException.class, () -> greetingService.getGreetings(new GreetingsFilter()));
	}

	@Test
	public void testGetGreetings() {
		List<BigInteger> greetingIds = List.of(new BigInteger(String.valueOf(123)), new BigInteger(String.valueOf(-1)));
		List<Greeting> greetingList = List.of(new Greeting());

		Mockito.when(greetingRepositoryMock.findAllById(greetingIds)).thenReturn(greetingList);

		assertEquals(greetingList, greetingService.getGreetings(new GreetingsFilter(greetingIds)));
	}

	@Test
	public void testGetGreetingsAndExceptionWasThrown() {
		List<BigInteger> greetingIds = List.of(new BigInteger(String.valueOf(123)), new BigInteger(String.valueOf(-1)));

		Mockito.when(greetingRepositoryMock.findAllById(greetingIds)).thenReturn(List.of());

		assertThrows(GreetingNotFoundException.class,
				() -> greetingService.getGreetings(new GreetingsFilter(greetingIds)));
	}

	@Test
	public void testCreateInMemoryGreeting() {
		Greeting greeting = new Greeting();

		Mockito.when(greetingRepositoryMock.save(any())).thenReturn(greeting);

		assertEquals(greeting, greetingService.createGreeting("Hello!!!"));
	}

	@Test
	public void testCreateInMemoryGreetingWhenExceptionThrown() {
		Mockito.when(greetingRepositoryMock.save(any())).thenThrow(exception);

		assertThrows(GreetingCreationException.class, () -> greetingService.createGreeting("123"));
	}

	@Test
	public void testUpdateInMemoryGreeting() {
		GreetingInput greetingInputMock = Mockito.mock(GreetingInput.class);
		BigInteger greetingId = BigInteger.valueOf(1L);
		String message = "updated an in memory greeting";
		Greeting greeting = new Greeting(greetingId, message);

		Mockito.when(greetingInputMock.getId()).thenReturn(greetingId, greetingId);
		Mockito.when(greetingInputMock.getGreeting()).thenReturn(message, message);
		Mockito.when(greetingRepositoryMock.setGreetingMessageById(message, greetingId)).thenReturn(1);

		assertEquals(greeting, greetingService.updateInMemoryGreeting(greetingInputMock));
	}

	@Test
	public void testUpdateInMemoryGreetingWhenRowIsNotUpdated() {
		GreetingInput greetingInputMock = Mockito.mock(GreetingInput.class);
		BigInteger greetingId = BigInteger.valueOf(1L);

		Mockito.when(greetingInputMock.getId()).thenReturn(greetingId);
		Mockito.when(greetingInputMock.getGreeting()).thenReturn(Strings.EMPTY);
		Mockito.when(greetingRepositoryMock.setGreetingMessageById(Strings.EMPTY, greetingId)).thenReturn(0);

		assertThrows(GreetingNotUpdatedException.class,
				() -> greetingService.updateInMemoryGreeting(greetingInputMock));
	}

	@Test
	public void testDeleteInMemoryGreeting() {
		BigInteger greetingId = new BigInteger(String.valueOf(123));
		String deleteResponse = "Deleted greeting with ID:" + greetingId;

		assertEquals(deleteResponse, greetingService.deleteInMemoryGreeting(greetingId));
	}

	@Test
	public void testDeleteInMemoryGreetingWhenExceptionIsThrown() {
		BigInteger greetingId = new BigInteger(String.valueOf(123));
		doThrow(exception).when(greetingRepositoryMock).deleteById(any());

		assertThrows(GreetingDeletionException.class, () -> greetingService.deleteInMemoryGreeting(greetingId));
	}
}

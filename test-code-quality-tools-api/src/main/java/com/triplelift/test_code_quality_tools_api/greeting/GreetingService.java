package com.triplelift.test_code_quality_tools_api.greeting;

import com.triplelift.test_code_quality_tools_api.exceptions.GreetingCreationException;
import com.triplelift.test_code_quality_tools_api.exceptions.GreetingDeletionException;
import com.triplelift.test_code_quality_tools_api.exceptions.GreetingNotFoundException;
import com.triplelift.test_code_quality_tools_api.exceptions.GreetingNotUpdatedException;
import com.triplelift.gen.types.GreetingInput;
import com.triplelift.gen.types.GreetingsFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
public class GreetingService {

	private final GreetingRepository greetingRepository;

	@Autowired
	public GreetingService(GreetingRepository greetingRepository) {
		this.greetingRepository = greetingRepository;
	}

	@NotNull
	@Cacheable(value = "greeting_cache", key = "#id.toString()")
	public Greeting getInMemoryGreeting(BigInteger id) {
		Optional<Greeting> greeting = greetingRepository.findById(id);
		greeting.orElseThrow(() -> new GreetingNotFoundException("greeting not found"));
		return greeting.get();
	}

	public List<Greeting> getGreetings(GreetingsFilter filter) {
		Optional<List<Greeting>> greetingsOptional;
		if (filter.getIds() == null || filter.getIds().isEmpty()) {
			greetingsOptional = Optional.of(greetingRepository.findAll());
		} else {
			greetingsOptional = Optional.of(greetingRepository.findAllById(filter.getIds()).stream()
					.filter(Objects::nonNull).collect(Collectors.toList()));
		}
		greetingsOptional.ifPresent(greetings -> {
			if (greetings.isEmpty()) {
				throw new GreetingNotFoundException("cannot find any Greetings");
			}
		});
		return greetingsOptional.get();
	}

	public Greeting createGreeting(String greeting) {
		try {
			return greetingRepository.save(Greeting.builder().message(greeting).build());
		} catch (Exception e) {
			throw new GreetingCreationException("Failed to create new Greeting");
		}
	}

	@Transactional
	@CacheEvict(value = "greeting_cache", key = "#greetingInput.getId().toString()")
	public Greeting updateInMemoryGreeting(GreetingInput greetingInput) {
		try {
			int updatedRowCount = greetingRepository.setGreetingMessageById(greetingInput.getGreeting(),
					greetingInput.getId());
			if (updatedRowCount == 0) {
				throw new GreetingNotUpdatedException("greeting not updated");
			}
			return new Greeting(greetingInput.getId(), greetingInput.getGreeting());
		} catch (Exception e) {
			throw new GreetingNotUpdatedException(e.getMessage());
		}
	}

	public String deleteInMemoryGreeting(BigInteger id) {
		try {
			greetingRepository.deleteById(id);
			return "Deleted greeting with ID:" + id;
		} catch (Exception e) {
			throw new GreetingDeletionException(e.getMessage());
		}
	}
}

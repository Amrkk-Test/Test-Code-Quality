package com.triplelift.test_code_quality_tools_api.greeting;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface GreetingRepository extends JpaRepository<Greeting, BigInteger> {

	@Modifying
	@Query("update Greeting g set g.message = ?1 where g.id = ?2")
	int setGreetingMessageById(String message, BigInteger id);

	Optional<Greeting> findById(@NotNull BigInteger id);
}

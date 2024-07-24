package com.triplelift.test_code_quality_tools_api.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.mockito.Mockito;

import java.util.List;

public class AudienceValidatorTest {
	Jwt jwtMock = Mockito.mock(Jwt.class);

	@Test
	public void OAuth2TokenValidatorResultDoesContainAudience() {
		AudienceValidator audience = new AudienceValidator("test1");
		OAuth2TokenValidatorResult result = audienceValidation(audience);
		Assertions.assertFalse(result.hasErrors());
		Mockito.verify(jwtMock, Mockito.times(1)).getAudience();
	}

	@Test
	public void OAuth2TokenValidatorResultDoesNotContainAudience() {
		AudienceValidator audience = new AudienceValidator("test");
		OAuth2TokenValidatorResult result = audienceValidation(audience);
		Assertions.assertTrue(result.hasErrors());
		Mockito.verify(jwtMock, Mockito.times(1)).getAudience();
	}

	private OAuth2TokenValidatorResult audienceValidation(AudienceValidator audienceValidator) {
		List<String> audiences = List.of("test1");
		Mockito.doReturn(audiences).when(jwtMock).getAudience();
		return audienceValidator.validate(jwtMock);
	}

}

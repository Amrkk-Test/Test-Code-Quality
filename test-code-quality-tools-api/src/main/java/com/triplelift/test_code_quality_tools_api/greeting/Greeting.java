package com.triplelift.test_code_quality_tools_api.greeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigInteger;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Greeting {

	@Id
	@GeneratedValue
	private BigInteger id;

	private String message;
}

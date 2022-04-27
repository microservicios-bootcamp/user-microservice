package com.demo.app.user.entities;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.demo.app.user.services.PersonalServiceTest;
import com.demo.app.user.services.impl.PersonalServiceImpl;
import com.demo.app.user.services.impl.PersonalServiceImplTests;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class PersonalServiceImplTest {

	private static PersonalServiceImplTests serviceTest;

	@BeforeAll
	public static void setUp() {
		serviceTest = new PersonalServiceImplTests();
	}

	@Test
	public void FinancialDebt() {
		boolean deuda = false;
		String dni = "123";
		Mono<Boolean> valorResultado = serviceTest.FinancialDebt(dni);
//	   assertEquals(Mono.just(deuda), valorResultado);
		StepVerifier.create(valorResultado).expectNext(deuda).verifyComplete();
		System.out.println(deuda);
		System.out.println(valorResultado);
	}

}

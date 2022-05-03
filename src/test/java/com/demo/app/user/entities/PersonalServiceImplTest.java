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
	
	@Test
	public void DebtPay() {
		String dni = "1234";
		Double amount = 20.0;
		String account = "credit acount";
		Double debtPay = 25.0;
		Double debt = 5.0;
		
		Mono<Double> debPay = serviceTest.DebtPay(dni, amount, account, debtPay);
		
		StepVerifier.create(debPay).expectNext(debt).verifyComplete();
		System.out.println(debPay);
	}

}

package com.demo.app.user.services.impl;

import org.springframework.stereotype.Service;

import com.demo.app.user.services.PersonalServiceTest;

import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@Service
public class PersonalServiceImplTests implements  PersonalServiceTest{

	@Override
	public Mono<Boolean> FinancialDebt(String dni) {
			if (dni == "123") {
				 return Mono.just(false);
			}
		 return Mono.just(true);
	}

	@Override
	public Mono<Double> DebtPay(String dni, Double amount, String account, Double debt) {
		if (debt > amount) {
			dni = "123";
			account = "credit account";
			return Mono.just(debt - amount);
		}
		
		return Mono.just(0.0);
	}
}

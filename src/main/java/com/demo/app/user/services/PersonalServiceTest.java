package com.demo.app.user.services;

import reactor.core.publisher.Mono;

public interface PersonalServiceTest {

    Mono<Boolean> FinancialDebt(String dni);
    Mono<Double> DebtPay(String dni, Double amount, String account, Double debt);
}

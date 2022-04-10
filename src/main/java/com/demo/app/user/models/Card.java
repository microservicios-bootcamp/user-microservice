package com.demo.app.user.models;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class Card {
    private AccountType accountType;
    private BigDecimal balance;
    private TypeCurrency currency;
    private String accountNumber;
    private Integer cvc;
    private CardType cardType;
    private String dni;
}

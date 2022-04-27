package com.demo.app.user.services.impl;

import com.demo.app.user.entities.Personal;
import com.demo.app.user.models.*;
import com.demo.app.user.repositories.PersonalRepository;
import com.demo.app.user.services.PersonalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class PersonalServiceImpl implements PersonalService {

    private final PersonalRepository personalRepository;
    private final WebClient webClientPasiveCard;
    private final WebClient webClientActiveCard;


    public PersonalServiceImpl(PersonalRepository personalRepository, WebClient.Builder webClientPasiveCard, WebClient.Builder webClientActiveCard
            , @Value("${pasive.card}") String pasiveCardUrl, @Value("${active.card}") String activeCardUrl) {
        this.personalRepository = personalRepository;
        this.webClientPasiveCard = webClientPasiveCard.baseUrl(pasiveCardUrl).build();
        this.webClientActiveCard = webClientActiveCard.baseUrl(activeCardUrl).build();
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Personal> findAll() {
        return personalRepository.findAll();
    }

    private Mono<CurrentAccount> createCurrentAccount(CurrentAccount card) {
        return webClientPasiveCard.post().uri("/currentAccount").
                body(Mono.just(card), CurrentAccount.class).
                retrieve().bodyToMono(CurrentAccount.class);
    }
    private Mono<SavingAccount> createSavingAccount(SavingAccount card) {
        return webClientPasiveCard.post().uri("/savingAccount").
                body(Mono.just(card), SavingAccount.class).
                retrieve().bodyToMono(SavingAccount.class);
    }
    private Mono<FixedTermAccount> createFixedTermAccount(FixedTermAccount card) {
        return webClientPasiveCard.post().uri("/fixedTermAccount").
                body(Mono.just(card), FixedTermAccount.class).
                retrieve().bodyToMono(FixedTermAccount.class);
    }

    private Mono<Boolean> findCurrentAccountByDni(String dni){
        return webClientPasiveCard.get().uri("/currentAccount/dni/" + dni).
                retrieve().bodyToMono(Boolean.class);
    }
    private Mono<Boolean> findSavingAccountByDni(String dni){
        return webClientPasiveCard.get().uri("/savingAccount/dni/" + dni).
                retrieve().bodyToMono(Boolean.class);
    }
    private Mono<Boolean> findFixedTermAccountByDni(String dni){
        return webClientPasiveCard.get().uri("/fixedTermAccount/dni/" + dni).
                retrieve().bodyToMono(Boolean.class);
    }

    private Mono<Boolean> createCreditAccount(List<CreditAccount> cards) {
        return webClientActiveCard.post().uri("/creditAccount/all").
                body(Flux.fromIterable(cards), CreditAccount.class).
                retrieve().bodyToFlux(CreditAccount.class).then(Mono.just(true));
    }
    private Mono<Boolean> findAllCreditAccountByDni(String dni){
        return webClientActiveCard.get().uri("/creditAccount/all/dni/" + dni).
                retrieve().bodyToFlux(CreditAccount.class).hasElements().flatMap(x->{
            if(x)return Mono.just(true);
            return Mono.just(false);
        });
    }

    private Mono<Boolean> findCardsDuplicated(String dni) {
        return Mono.zip(findSavingAccountByDni(dni), findCurrentAccountByDni(dni), findFixedTermAccountByDni(dni))
                .map(account -> {
                    if (account.getT1().equals(false) && account.getT2().equals(false) && account.getT3().equals(false)) {
                        return false;
                    }
                    return true;
                });
    }


    private Mono<Personal> findAndCreatePersonalAccount(Personal personal, SavingAccount savingAccount) {
        return findCardsDuplicated(personal.getDni()).flatMap(x->{
            if(!x) return Mono.zip(personalRepository.save(personal),createSavingAccount(savingAccount))
                    .map(account -> {
                        account.getT1();
                        return account.getT2();
                    });
            return Mono.just(personal);
        }).thenReturn(personal);
    }

    @Override
    @Transactional
    public Mono<Personal> saveNormalSavingAccount(Personal personal) {
        SavingAccount savingAccount = personal.getSavingAccount();
        savingAccount.setDni(personal.getDni());
        savingAccount.setType(SavingAccountType.NORMAL);
        return findAndCreatePersonalAccount(personal, savingAccount);
    }

    @Override
    public Mono<Personal> saveVipSavingAccount(Personal personal) {
        SavingAccount savingAccount = personal.getSavingAccount();
        savingAccount.setDni(personal.getDni());
        savingAccount.setType(SavingAccountType.VIP);
        return Mono.zip(findAllCreditAccountByDni(personal.getDni()),findAndCreatePersonalAccount(personal, savingAccount))
                .map(account->{
                    System.out.println(account.getT1());
                    if(account.getT1().equals(false)) return Mono.empty();
                    return account.getT2();
                }).thenReturn(personal);
    }

    @Override
    @Transactional
    public Mono<Personal> saveFixedTermAccount(Personal personal) {
        FixedTermAccount fixedTermAccount = personal.getFixedTermAccount();
        fixedTermAccount.setDni(personal.getDni());
        return findCardsDuplicated(personal.getDni()).flatMap(x->{
            if(!x) return Mono.zip(personalRepository.save(personal), createFixedTermAccount(fixedTermAccount))
                    .map(account -> {
                        account.getT1();
                        return account.getT2();
                    });
            return Mono.empty();
        }).thenReturn(personal);
    }

    @Override
    @Transactional
    public Mono<Personal> saveCurrentAccount(Personal personal) {
        CurrentAccount currentAccount = personal.getCurrentAccount();
        currentAccount.setDni(personal.getDni());
        return findCardsDuplicated(personal.getDni()).flatMap(x->{
            if(!x) return Mono.zip(personalRepository.save(personal), createCurrentAccount(currentAccount))
                    .map(account -> {
                        account.getT1();
                        return account.getT2();
                    });
            return Mono.empty();
        }).thenReturn(personal);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Personal> findById(String id) {
        return personalRepository.findById(id);
    }

    @Override
    public Mono<Personal> update(Personal personal,String id) {
        return personalRepository.findById(id).flatMap(x->{
            x.setName(personal.getName());
            x.setLastName(personal.getLastName());
            x.setEmail(personal.getEmail());
            x.setNumber(personal.getNumber());
            return personalRepository.save(x);
        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return personalRepository.deleteById(id);
    }


}

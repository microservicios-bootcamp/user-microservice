package com.demo.app.user.services.impl;

import com.demo.app.user.entities.Personal;
import com.demo.app.user.models.CurrentAccount;
import com.demo.app.user.models.FixedTermAccount;
import com.demo.app.user.models.SavingAccount;
import com.demo.app.user.repositories.PersonalRepository;
import com.demo.app.user.services.PersonalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class PersonalServiceImpl implements PersonalService {

    private final PersonalRepository personalRepository;

    private final WebClient webClient;

    public PersonalServiceImpl(PersonalRepository personalRepository, WebClient.Builder webClient) {
        this.personalRepository = personalRepository;
        this.webClient = webClient.baseUrl("http://localhost:8022").build();
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Personal> findAll() {
        return personalRepository.findAll();
    }

    private Mono<CurrentAccount> createCurrentAccount(CurrentAccount card) {
        return webClient.post().uri("/currentAccount").
                body(Mono.just(card), CurrentAccount.class).
                retrieve().bodyToMono(CurrentAccount.class);
    }
    private Mono<SavingAccount> createSavingAccount(SavingAccount card) {
        return webClient.post().uri("/savingAccount").
                body(Mono.just(card), SavingAccount.class).
                retrieve().bodyToMono(SavingAccount.class);
    }
    private Mono<FixedTermAccount> createFixedTermAccount(FixedTermAccount card) {
        return webClient.post().uri("/fixedTermAccount").
                body(Mono.just(card), FixedTermAccount.class).
                retrieve().bodyToMono(FixedTermAccount.class);
    }

    private Mono<Boolean> findCurrentAccountByDni(String dni){
        return webClient.get().uri("/currentAccount/dni/" + dni).
                retrieve().bodyToMono(Boolean.class);
    }
    private Mono<Boolean> findSavingAccountByDni(String dni){
        return webClient.get().uri("/savingAccount/dni/" + dni).
                retrieve().bodyToMono(Boolean.class);
    }
    private Mono<Boolean> findFixedTermAccountByDni(String dni){
        return webClient.get().uri("/fixedTermAccount/dni/" + dni).
                retrieve().bodyToMono(Boolean.class);
    }


    private Mono<Boolean> findCardsDuplicated(String dni) {
        Mono<Boolean> result = Mono.zip(findSavingAccountByDni(dni), findCurrentAccountByDni(dni), findFixedTermAccountByDni(dni))
                .map(account -> {
                    if (account.getT1().equals(false) && account.getT2().equals(false) && account.getT3().equals(false)) {
                        return account.getT1();
                    }
                    return !account.getT1();
                });
        return result;
    }

    @Override
    @Transactional
    public Mono<Personal> saveSavingAccount(Personal personal) {
        SavingAccount savingAccount = personal.getSavingAccount();
        savingAccount.setDni(personal.getDni());
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
            x.setDni(personal.getDni());
            x.setNumber(personal.getNumber());
            return personalRepository.save(x);
        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return personalRepository.deleteById(id);
    }
}

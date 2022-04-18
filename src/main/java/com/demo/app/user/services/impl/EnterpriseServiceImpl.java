package com.demo.app.user.services.impl;

import com.demo.app.user.entities.Enterprise;
import com.demo.app.user.models.CreditAccount;
import com.demo.app.user.models.CurrentAccount;
import com.demo.app.user.repositories.EnterpriseRepository;
import com.demo.app.user.services.EnterpriseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final WebClient webClientPasiveCard;
    private final WebClient webClientActiveCard;

    public EnterpriseServiceImpl(EnterpriseRepository enterpriseRepository, WebClient.Builder webClientPasiveCard, WebClient.Builder webClientActiveCard
            ,@Value("${pasive.card}") String pasiveCardUrl,@Value("${active.card}") String activeCardUrl) {
        this.enterpriseRepository = enterpriseRepository;
        this.webClientPasiveCard = webClientPasiveCard.baseUrl(pasiveCardUrl).build();
        this.webClientActiveCard = webClientActiveCard.baseUrl(activeCardUrl).build();
    }

    private Mono<Boolean> createCurrentAccount(List<CurrentAccount> cards) {
        return webClientPasiveCard.post().uri("/currentAccount/all").
                body(Flux.fromIterable(cards), CurrentAccount.class).
                retrieve().bodyToFlux(CurrentAccount.class).then(Mono.just(true));
    }
    private Mono<Boolean> findAllCurrentAccountByDni(String dni){
        return webClientPasiveCard.get().uri("/currentAccount/all/dni/" + dni).
                retrieve().bodyToFlux(CurrentAccount.class).hasElements().flatMap(x->{
                if(x)return Mono.just(true);
                return Mono.just(false);
        });
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
    @Override
    public Flux<Enterprise> findAll() {
        return enterpriseRepository.findAll();
    }

    @Override
    public Mono<Enterprise> saveCurrentAccount(Enterprise enterprise) {
        return findAllCurrentAccountByDni(enterprise.getDni()).flatMap(x->{
            if(!x){
                List<CurrentAccount> cards = enterprise.getCards();
                cards.forEach(card -> card.setDni(enterprise.getDni()));
                Mono<Boolean> accounts = createCurrentAccount(cards);
                return Mono.zip(enterpriseRepository.save(enterprise),accounts)
                        .map(result->{
                            result.getT1();
                            return result.getT2();
                        });
            }
            return Mono.empty();
        }).thenReturn(enterprise);
    }

    @Override
    public Mono<Enterprise> saveCreditAccount(Enterprise enterprise) {
        return findAllCreditAccountByDni(enterprise.getDni()).flatMap(x->{
            if(!x){
                List<CreditAccount> cards = enterprise.getCreditAccounts();
                cards.forEach(card -> card.setDni(enterprise.getDni()));
                Mono<Boolean> accounts = createCreditAccount(cards);
                return Mono.zip(enterpriseRepository.save(enterprise),accounts)
                        .map(result->{
                            result.getT1();
                            return result.getT2();
                        });
            }
            return Mono.empty();
        }).thenReturn(enterprise);
    }

    @Override
    public Mono<Enterprise> findById(String id) {
        return enterpriseRepository.findById(id);
    }

    @Override
    public Mono<Enterprise> update(Enterprise enterprise, String id) {
        return enterpriseRepository.findById(id).flatMap(x->{
            x.setName(enterprise.getName());
            x.setLastName(enterprise.getLastName());
            x.setEmail(enterprise.getEmail());
            x.setDni(enterprise.getDni());
            x.setNumber(enterprise.getNumber());
            x.setRuc(enterprise.getRuc());
            return enterpriseRepository.save(x);
        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return enterpriseRepository.deleteById(id);
    }
}

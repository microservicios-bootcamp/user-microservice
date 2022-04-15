package com.demo.app.user.services.impl;

import com.demo.app.user.entities.Enterprise;
import com.demo.app.user.models.CardType;
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
    private final WebClient webClient;

    public EnterpriseServiceImpl(EnterpriseRepository enterpriseRepository, WebClient.Builder webClient,@Value("${pasive.card}") String pasiveCardUrl) {
        this.enterpriseRepository = enterpriseRepository;
        this.webClient = webClient.baseUrl(pasiveCardUrl).build();
    }

    private Mono<Boolean> createCurrentAccount(List<CurrentAccount> cards) {
        return webClient.post().uri("/currentAccount/all").
                body(Flux.fromIterable(cards), CurrentAccount.class).
                retrieve().bodyToFlux(CurrentAccount.class).then(Mono.just(true));
    }
    private Mono<Boolean> findAllCurrentAccountByDni(String dni){
        return webClient.get().uri("/currentAccount/all/dni/" + dni).
                retrieve().bodyToFlux(CurrentAccount.class).hasElements().flatMap(x->{
                if(x)return Mono.just(true);
                return Mono.just(false);
        });
    }
    @Override
    public Flux<Enterprise> findAll() {
        return enterpriseRepository.findAll();
    }

    @Override
    public Mono<Enterprise> save(Enterprise enterprise,CardType type) {
        return findAllCurrentAccountByDni(enterprise.getDni()).flatMap(x->{
            if(!x){
                Mono<Boolean> accounts;
                List<CurrentAccount> cards = enterprise.getCards();
                cards.stream().forEach(card -> {
                    card.setDni(enterprise.getDni());
                });
                if(type == CardType.DEBITO) accounts=createCurrentAccount(cards);
                else accounts=createCurrentAccount(cards);
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

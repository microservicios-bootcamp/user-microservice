package com.demo.app.user.services.impl;

import com.demo.app.user.entities.Enterprise;
import com.demo.app.user.models.AccountType;
import com.demo.app.user.models.Card;
import com.demo.app.user.models.CardType;
import com.demo.app.user.repositories.EnterpriseRepository;
import com.demo.app.user.services.EnterpriseService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final WebClient webClient;

    public EnterpriseServiceImpl(EnterpriseRepository enterpriseRepository, WebClient.Builder webClient) {
        this.enterpriseRepository = enterpriseRepository;
        this.webClient = webClient.baseUrl("http://localhost:8022").build();
    }
    private Mono<Boolean> createCards(List<Card> cards) {
        return webClient.post().uri("/card/all").
                body(Flux.fromIterable(cards), Card.class).
                retrieve().bodyToFlux(Card.class).then(Mono.just(true));
    }

    private Mono<Boolean> findCardByDniAndCardType(String dni,CardType type){
        return webClient.get().uri("/card/dni/" + dni+"/type/"+type).
                retrieve().bodyToMono(Boolean.class);
    }
    @Override
    public Flux<Enterprise> findAll() {
        return enterpriseRepository.findAll();
    }

    @Override
    public Mono<Enterprise> save(Enterprise enterprise, CardType type) {
        return findCardByDniAndCardType(enterprise.getDni(),type).flatMap(x->{
            if(!x){
                List<Card> cards = enterprise.getCards();
                if(type == CardType.DEBITO) cards.stream().forEach(card -> card.setAccountType(AccountType.CUENTA_CORRIENTE));
                else cards.stream().forEach(card->card.setAccountType(AccountType.CREDITO));
                cards.stream().forEach(card -> {
                    card.setDni(enterprise.getDni());
                    card.setCardType(type);
                });
                return Mono.zip(enterpriseRepository.save(enterprise),createCards(cards))
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
            x.setCards(enterprise.getCards());
            x.getCards().stream().forEach(card->{
                if(card.getAccountType() != AccountType.CREDITO) card.setAccountType(AccountType.CUENTA_CORRIENTE);
            });
            return enterpriseRepository.save(x);
        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return enterpriseRepository.deleteById(id);
    }
}

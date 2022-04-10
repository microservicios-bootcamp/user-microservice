package com.demo.app.user.services.impl;

import com.demo.app.user.entities.Personal;
import com.demo.app.user.models.AccountType;
import com.demo.app.user.models.Card;
import com.demo.app.user.models.CardType;
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

    private Mono<Card> createCard(Card card) {
        return webClient.post().uri("/card").
                body(Mono.just(card), Card.class).
                retrieve().bodyToMono(Card.class);
    }

    private Mono<Boolean> findCardByDniAndCardType(String dni,CardType type){
        return webClient.get().uri("/card/dni/" + dni+"/type/"+type).
                retrieve().bodyToMono(Boolean.class);
    }

    @Override
    @Transactional
    public Mono<Personal> save(Personal personal,CardType type) {
        return findCardByDniAndCardType(personal.getDni(),type).flatMap(x->{
            if(!x){
                Card card = personal.getCard();
                if(type == CardType.CREDITO) card.setAccountType(AccountType.CREDITO);
                card.setDni(personal.getDni());
                card.setCardType(type);
                return Mono.zip(personalRepository.save(personal),createCard(card))
                        .map(result->{
                            result.getT1();
                            return result.getT2();
                        });
            }
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

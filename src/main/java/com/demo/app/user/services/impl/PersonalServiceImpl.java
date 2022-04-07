package com.demo.app.user.services.impl;

import com.demo.app.user.entities.Personal;
import com.demo.app.user.repositories.PersonalRepository;
import com.demo.app.user.services.PersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class PersonalServiceImpl implements PersonalService {

    private final PersonalRepository personalRepository;

    public PersonalServiceImpl(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Personal> findAll() {
        return personalRepository.findAll();
    }

    @Override
    public Mono<Personal> save(Personal personal) {
        return personalRepository.save(personal);
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

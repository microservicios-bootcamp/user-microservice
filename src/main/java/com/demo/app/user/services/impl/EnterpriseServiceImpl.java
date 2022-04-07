package com.demo.app.user.services.impl;

import com.demo.app.user.entities.Enterprise;
import com.demo.app.user.repositories.EnterpriseRepository;
import com.demo.app.user.services.EnterpriseService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    public EnterpriseServiceImpl(EnterpriseRepository enterpriseRepository) {
        this.enterpriseRepository = enterpriseRepository;
    }

    @Override
    public Flux<Enterprise> findAll() {
        return enterpriseRepository.findAll();
    }

    @Override
    public Mono<Enterprise> save(Enterprise enterprise) {
        return enterpriseRepository.save(enterprise);
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

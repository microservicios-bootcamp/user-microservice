package com.demo.app.user.services;

import com.demo.app.user.entities.Enterprise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EnterpriseService {
    Flux<Enterprise> findAll();
    Mono<Enterprise> save(Enterprise enterprise);
    Mono<Enterprise> findById(String id);
    Mono<Enterprise> update(Enterprise enterprise,String id);
    Mono<Void> delete(String id);
}

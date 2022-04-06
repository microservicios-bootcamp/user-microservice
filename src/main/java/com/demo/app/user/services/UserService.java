package com.demo.app.user.services;

import com.demo.app.user.entities.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface UserService {
    Flux<User> findAll();
    Mono<User> save(User user);
    Mono<User> findById(String id);
    Mono<User> update(User user,String id);
    Mono<Void> delete(String id);
}

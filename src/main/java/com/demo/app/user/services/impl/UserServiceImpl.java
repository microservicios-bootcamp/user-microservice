package com.demo.app.user.services.impl;

import com.demo.app.user.entities.User;
import com.demo.app.user.repositories.UserRepository;
import com.demo.app.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> update(User user,String id) {
        return userRepository.findById(id).flatMap(x->{
            x.setName(user.getName());
            x.setLastName(user.getLastName());
            x.setEmail(user.getEmail());
            return userRepository.save(x);
        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return userRepository.deleteById(id);
    }
}

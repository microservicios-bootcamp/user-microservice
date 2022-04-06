package com.demo.app.user.controllers;

import com.demo.app.user.entities.User;
import com.demo.app.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    private ResponseEntity<Flux<User>> findAll(){
        Flux<User> user = userService.findAll();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    private Mono<ResponseEntity<User>> findById(@PathVariable String id){
        return userService.findById(id).map(x->ResponseEntity.ok(x)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Mono<User>> save(@RequestBody User user){
        return ResponseEntity.ok(userService.save(user));
    }
    @PutMapping("/{id}")
    private Mono<ResponseEntity<User>> update(@RequestBody User user, @PathVariable String id){
        return userService.update(user,id).map(x->ResponseEntity.ok(x)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    private Mono<ResponseEntity<Void>> delete(@PathVariable String id){
        return userService.delete(id).map(x->ResponseEntity.ok(x)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

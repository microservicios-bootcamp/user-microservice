package com.demo.app.user.controllers;

import com.demo.app.user.entities.Personal;
import com.demo.app.user.services.PersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {
    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @GetMapping
    private ResponseEntity<Flux<Personal>> findAll(){
        Flux<Personal> personal = personalService.findAll();
        return ResponseEntity.ok(personal);
    }

    @GetMapping("/{id}")
    private Mono<ResponseEntity<Personal>> findById(@PathVariable String id){
        return personalService.findById(id).map(x->ResponseEntity.ok(x)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Mono<Personal>> save(@RequestBody Personal personal){
        return ResponseEntity.ok(personalService.save(personal));
    }
    @PutMapping("/{id}")
    private Mono<ResponseEntity<Personal>> update(@RequestBody Personal personal, @PathVariable String id){
        return personalService.update(personal,id).map(x->ResponseEntity.ok(x)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    private Mono<ResponseEntity<Void>> delete(@PathVariable String id){
        return personalService.delete(id).map(x->ResponseEntity.ok(x)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

package com.demo.app.user.controllers;

import com.demo.app.user.entities.Personal;
import com.demo.app.user.models.SavingAccountType;
import com.demo.app.user.services.PersonalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/personal")
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
        return personalService.findById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/saveCurrentAccount")
    private ResponseEntity<Mono<Personal>> saveCurrentAccount(@RequestBody Personal personal){
        return ResponseEntity.ok(personalService.saveCurrentAccount(personal));
    }
    @PostMapping("/saveSavingAccount/{type}")
    private ResponseEntity<Mono<Personal>> saveSavingAccount(@RequestBody Personal personal, @PathVariable SavingAccountType type){
        Mono<Personal> result;
        if (type.equals(SavingAccountType.NORMAL)) result = personalService.saveNormalSavingAccount(personal);
        else result = personalService.saveVipSavingAccount(personal);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/saveFixedTermAccount")
    private ResponseEntity<Mono<Personal>> saveFixedTermAccount(@RequestBody Personal personal){
        return ResponseEntity.ok(personalService.saveFixedTermAccount(personal));
    }
    @PutMapping("/{id}")
    private Mono<ResponseEntity<Personal>> update(@RequestBody Personal personal, @PathVariable String id){
        return personalService.update(personal,id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    private Mono<ResponseEntity<Void>> delete(@PathVariable String id){
        return personalService.delete(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

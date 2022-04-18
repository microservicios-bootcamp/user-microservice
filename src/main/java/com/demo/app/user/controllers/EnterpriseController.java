package com.demo.app.user.controllers;

import com.demo.app.user.entities.Enterprise;
import com.demo.app.user.entities.Personal;
import com.demo.app.user.models.CurrentAccountType;
import com.demo.app.user.models.SavingAccountType;
import com.demo.app.user.services.EnterpriseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {
    private final EnterpriseService enterpriseService;

    public EnterpriseController(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
    }

    @GetMapping
    private ResponseEntity<Flux<Enterprise>> findAll(){
        Flux<Enterprise> enterprise = enterpriseService.findAll();
        return ResponseEntity.ok(enterprise);
    }

    @GetMapping("/{id}")
    private Mono<ResponseEntity<Enterprise>> findById(@PathVariable String id){
        return enterpriseService.findById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("saveCurrentAccount/{type}")
    private ResponseEntity<Mono<Enterprise>> saveCurrentAccount(@RequestBody Enterprise enterprise,@PathVariable CurrentAccountType type){
        Mono<Enterprise> result;
        if (type.equals(CurrentAccountType.NORMAL)) result = enterpriseService.saveNormalCurrentAccount(enterprise);
        else result = enterpriseService.savePymeCurrentAccount(enterprise);
        return ResponseEntity.ok(result);
    }
    @PostMapping("saveCreditAccount")
    private ResponseEntity<Mono<Enterprise>> saveCreditAccount(@RequestBody Enterprise enterprise){
        return ResponseEntity.ok(enterpriseService.saveCreditAccount(enterprise));
    }
    @PutMapping("/{id}")
    private Mono<ResponseEntity<Enterprise>> update(@RequestBody Enterprise enterprise, @PathVariable String id){
        return enterpriseService.update(enterprise,id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    private Mono<ResponseEntity<String>> delete(@PathVariable String id){
        return enterpriseService.delete(id).map(x->ResponseEntity.ok("Id eliminado"+id)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

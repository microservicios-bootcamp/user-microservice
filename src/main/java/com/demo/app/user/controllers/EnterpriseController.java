package com.demo.app.user.controllers;

import com.demo.app.user.entities.Enterprise;
import com.demo.app.user.services.EnterpriseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/enterprise")
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
        return enterpriseService.findById(id).map(x->ResponseEntity.ok(x)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Mono<Enterprise>> save(@RequestBody Enterprise enterprise){
        return ResponseEntity.ok(enterpriseService.save(enterprise));
    }
    @PutMapping("/{id}")
    private Mono<ResponseEntity<Enterprise>> update(@RequestBody Enterprise enterprise, @PathVariable String id){
        return enterpriseService.update(enterprise,id).map(x->ResponseEntity.ok(x)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    private Mono<ResponseEntity<String>> delete(@PathVariable String id){
        return enterpriseService.delete(id).map(x->ResponseEntity.ok("Id eliminado"+id)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

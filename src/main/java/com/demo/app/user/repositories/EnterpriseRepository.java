package com.demo.app.user.repositories;

import com.demo.app.user.entities.Enterprise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseRepository extends ReactiveMongoRepository<Enterprise,String> {
}

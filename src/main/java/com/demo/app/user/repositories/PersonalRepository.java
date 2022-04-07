package com.demo.app.user.repositories;

import com.demo.app.user.entities.Personal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalRepository extends ReactiveMongoRepository<Personal,String> {

}

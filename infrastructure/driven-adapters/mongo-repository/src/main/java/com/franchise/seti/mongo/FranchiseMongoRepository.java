package com.franchise.seti.mongo;

import com.franchise.seti.mongo.entities.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
    Mono<Boolean> existsByName(String name);

    Mono<FranchiseDocument> findByName(String name);
}

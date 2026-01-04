package com.franchise.seti.mongo;

import com.franchise.seti.mongo.entities.BranchDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchMongoRepository extends ReactiveMongoRepository<BranchDocument, String> {
    Flux<BranchDocument> findByFranchiseId(String franchiseId);

    Mono<Boolean> existsByNameAndFranchiseId(String name, String franchiseId);
}

package com.franchise.seti.mongo;

import com.franchise.seti.mongo.entities.ProductDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductMongoRepository extends ReactiveMongoRepository<ProductDocument, String> {

    Flux<ProductDocument> findByBranchId(String branchId);

    Mono<Boolean> existsByNameAndBranchId(String name, String branchId);
}

package com.franchise.seti.model.gateways;

import com.franchise.seti.model.Product;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.ProductId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(ProductId id);
    Flux<Product> findByBranchId(BranchId branchId);
    Mono<Void> deleteById(ProductId id);

    // Optional but recommended for 409 conflict
    Mono<Boolean> existsByNameAndBranchId(String name, BranchId branchId);
}

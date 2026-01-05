package com.franchise.seti.model.gateways;

import com.franchise.seti.model.Franchise;
import com.franchise.seti.model.ids.FranchiseId;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(FranchiseId id);
    Mono<Boolean> existsByName(String name);
}

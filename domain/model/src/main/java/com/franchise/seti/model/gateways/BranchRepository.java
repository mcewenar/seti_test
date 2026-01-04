package com.franchise.seti.model.gateways;

import com.franchise.seti.model.Branch;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.FranchiseId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(BranchId id);
    Flux<Branch> findByFranchiseId(FranchiseId franchiseId);
    Mono<Boolean> existsByNameAndFranchiseId(String name, FranchiseId franchiseId); // optional but recommended (409)
}

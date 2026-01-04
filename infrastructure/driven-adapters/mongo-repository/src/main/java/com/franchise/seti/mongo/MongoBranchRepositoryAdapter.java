package com.franchise.seti.mongo;

import com.franchise.seti.model.Branch;
import com.franchise.seti.model.gateways.BranchRepository;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.FranchiseId;
import com.franchise.seti.mongo.entities.BranchDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MongoBranchRepositoryAdapter implements BranchRepository {

    private final BranchMongoRepository repository;

    @Override
    public Mono<Branch> save(Branch branch) {
        return repository.save(toDocument(branch))
                .map(this::toDomain);
    }

    @Override
    public Mono<Branch> findById(BranchId id) {
        return repository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Flux<Branch> findByFranchiseId(FranchiseId franchiseId) {
        return repository.findByFranchiseId(franchiseId.value())
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByNameAndFranchiseId(String name, FranchiseId franchiseId) {
        return repository.existsByNameAndFranchiseId(name, franchiseId.value());
    }

    private BranchDocument toDocument(Branch b) {
        return BranchDocument.builder()
                .id(b.getId().value())
                .franchiseId(b.getFranchiseId().value())
                .name(b.getName())
                .build();
    }

    private Branch toDomain(BranchDocument d) {
        return Branch.create(
                BranchId.of(d.getId()),
                FranchiseId.of(d.getFranchiseId()),
                d.getName()
        );
    }
}


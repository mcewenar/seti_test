package com.franchise.seti.mongo;

import com.franchise.seti.model.Franchise;
import com.franchise.seti.model.gateways.FranchiseRepository;
import com.franchise.seti.model.ids.FranchiseId;
import com.franchise.seti.mongo.entities.FranchiseDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MongoFranchiseRepositoryAdapter implements FranchiseRepository {

    private final FranchiseMongoRepository repository;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return repository.save(toDocument(franchise))
                .map(this::toDomain);
    }

    @Override
    public Mono<Franchise> findById(FranchiseId id) {
        return repository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        return repository.findByName(name)
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    private FranchiseDocument toDocument(Franchise f) {
        return FranchiseDocument.builder()
                .id(f.getId().value())
                .name(f.getName())
                .build();
    }

    private Franchise toDomain(FranchiseDocument d) {
        return Franchise.create(
                FranchiseId.of(d.getId()),
                d.getName()
        );
    }
}

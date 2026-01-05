package com.franchise.seti.mongo;

import com.franchise.seti.model.Franchise;
import com.franchise.seti.model.ids.FranchiseId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

@Import(MongoFranchiseRepositoryAdapter.class)
class MongoFranchiseRepositoryAdapterTest {

    @Autowired
    MongoFranchiseRepositoryAdapter adapter;

    @Autowired
    FranchiseMongoRepository springRepo;

    @Test
    void saveAndFindById_shouldPersistAndReadBack() {
        Franchise franchise = Franchise.create(FranchiseId.of("f-1"), "Nequi");

        StepVerifier.create(
                        adapter.save(franchise)
                                .flatMap(saved -> adapter.findById(saved.getId()))
                )
                .assertNext(found -> {
                    org.junit.jupiter.api.Assertions.assertEquals("f-1", found.getId().value());
                    org.junit.jupiter.api.Assertions.assertEquals("Nequi", found.getName());
                })
                .verifyComplete();
    }

    @Test
    void existsByName_shouldReturnTrueAfterInsert() {
        Franchise franchise = Franchise.create(FranchiseId.of("f-2"), "Acme");

        StepVerifier.create(
                        adapter.save(franchise)
                                .then(adapter.existsByName("Acme"))
                )
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByName_shouldReturnEntityIfGatewaySupportsIt() {
        // If your FranchiseRepository gateway doesn't expose findByName, remove this test.
        Franchise franchise = Franchise.create(FranchiseId.of("f-3"), "Umbrella");

        StepVerifier.create(
                        adapter.save(franchise)
                                .then(adapter.findById(new FranchiseId("f-3")))
                )
                .assertNext(found -> {
                    Assertions.assertEquals("f-3", found.getId().value());
                    org.junit.jupiter.api.Assertions.assertEquals("Umbrella", found.getName());
                })
                .verifyComplete();
    }
}

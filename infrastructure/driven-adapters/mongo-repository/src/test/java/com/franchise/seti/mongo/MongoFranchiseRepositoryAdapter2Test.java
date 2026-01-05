package com.franchise.seti.mongo;

import com.franchise.seti.model.Franchise;
import com.franchise.seti.model.ids.FranchiseId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

@Import(MongoFranchiseRepositoryAdapterTest.class) // bring adapter bean
class MongoFranchiseRepositoryAdapter2Test {


    @Autowired
    MongoFranchiseRepositoryAdapterTest adapter;

    @Autowired
    FranchiseMongoRepository springRepo;

    @Test
    void saveAndFindById_shouldWork() {
        var f = Franchise.create(FranchiseId.of("f-1"), "Nequi");

        StepVerifier.create(adapter.save(f)
                        .flatMap(saved -> adapter.findById(saved.getId())))
                .assertNext(found -> {
                    org.junit.jupiter.api.Assertions.assertEquals("f-1", found.getId().value());
                    org.junit.jupiter.api.Assertions.assertEquals("Nequi", found.getName());
                })
                .verifyComplete();
    }
}
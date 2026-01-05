package com.franchise.seti.mongo;

import com.franchise.seti.model.Branch;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.FranchiseId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

@Import(MongoBranchRepositoryAdapter.class)
class MongoBranchRepositoryAdapterTest {

    @Autowired
    MongoBranchRepositoryAdapter adapter;

    @Autowired
    BranchMongoRepository springRepo;

    @Test
    void saveAndFindById_shouldPersistAndReadBack() {
        FranchiseId fId = FranchiseId.of("f-10");
        Branch branch = Branch.create(BranchId.of("b-1"), fId, "Medellin");

        StepVerifier.create(
                        adapter.save(branch)
                                .flatMap(saved -> adapter.findById(saved.getId()))
                )
                .assertNext(found -> {
                    org.junit.jupiter.api.Assertions.assertEquals("b-1", found.getId().value());
                    org.junit.jupiter.api.Assertions.assertEquals("f-10", found.getFranchiseId().value());
                    org.junit.jupiter.api.Assertions.assertEquals("Medellin", found.getName());
                })
                .verifyComplete();
    }

    @Test
    void findByFranchiseId_shouldReturnBranchesBelongingToFranchise() {
        FranchiseId fId = FranchiseId.of("f-11");

        Branch b1 = Branch.create(BranchId.of("b-11"), fId, "Branch A");
        Branch b2 = Branch.create(BranchId.of("b-12"), fId, "Branch B");

        StepVerifier.create(
                        adapter.save(b1)
                                .then(adapter.save(b2))
                                .thenMany(adapter.findByFranchiseId(fId))
                                .map(Branch::getId)
                                .map(BranchId::value)
                                .collectList()
                )
                .assertNext(ids -> {
                    org.junit.jupiter.api.Assertions.assertTrue(ids.contains("b-11"));
                    org.junit.jupiter.api.Assertions.assertTrue(ids.contains("b-12"));
                })
                .verifyComplete();
    }

    @Test
    void existsByNameAndFranchiseId_shouldWork() {
        FranchiseId fId = FranchiseId.of("f-12");
        Branch b = Branch.create(BranchId.of("b-20"), fId, "HQ");

        StepVerifier.create(
                        adapter.save(b)
                                .then(adapter.existsByNameAndFranchiseId("HQ", fId))
                )
                .expectNext(true)
                .verifyComplete();
    }
}

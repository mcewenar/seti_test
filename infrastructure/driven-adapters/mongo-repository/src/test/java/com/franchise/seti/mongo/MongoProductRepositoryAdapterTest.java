package com.franchise.seti.mongo;

import com.franchise.seti.model.Product;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.ProductId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

@Import(MongoProductRepositoryAdapter.class)
class MongoProductRepositoryAdapterTest {

    @Autowired
    MongoProductRepositoryAdapter adapter;

    @Autowired
    ProductMongoRepository springRepo;

    @Test
    void saveAndFindById_shouldPersistAndReadBack() {
        BranchId bId = BranchId.of("b-100");
        Product product = Product.create(ProductId.of("p-1"), bId, "Coffee", 10);

        StepVerifier.create(
                        adapter.save(product)
                                .flatMap(saved -> adapter.findById(saved.getId()))
                )
                .assertNext(found -> {
                    org.junit.jupiter.api.Assertions.assertEquals("p-1", found.getId().value());
                    org.junit.jupiter.api.Assertions.assertEquals("b-100", found.getBranchId().value());
                    org.junit.jupiter.api.Assertions.assertEquals("Coffee", found.getName());
                    org.junit.jupiter.api.Assertions.assertEquals(10, found.getStock());
                })
                .verifyComplete();
    }

    @Test
    void findByBranchId_shouldReturnProductsBelongingToBranch() {
        BranchId bId = BranchId.of("b-200");

        Product p1 = Product.create(ProductId.of("p-10"), bId, "A", 1);
        Product p2 = Product.create(ProductId.of("p-11"), bId, "B", 2);

        StepVerifier.create(
                        adapter.save(p1)
                                .then(adapter.save(p2))
                                .thenMany(adapter.findByBranchId(bId))
                                .map(Product::getId)
                                .map(ProductId::value)
                                .collectList()
                )
                .assertNext(ids -> {
                    org.junit.jupiter.api.Assertions.assertTrue(ids.contains("p-10"));
                    org.junit.jupiter.api.Assertions.assertTrue(ids.contains("p-11"));
                })
                .verifyComplete();
    }

    @Test
    void existsByNameAndBranchId_shouldWork() {
        BranchId bId = BranchId.of("b-300");
        Product p = Product.create(ProductId.of("p-20"), bId, "Latte", 5);

        StepVerifier.create(
                        adapter.save(p)
                                .then(adapter.existsByNameAndBranchId("Latte", bId))
                )
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void deleteById_shouldRemoveDocument() {
        BranchId bId = BranchId.of("b-400");
        ProductId pId = ProductId.of("p-del");
        Product p = Product.create(pId, bId, "ToDelete", 1);

        StepVerifier.create(
                        adapter.save(p)
                                .then(adapter.deleteById(pId))
                                .then(adapter.findById(pId))
                )
                .verifyComplete();
    }
}


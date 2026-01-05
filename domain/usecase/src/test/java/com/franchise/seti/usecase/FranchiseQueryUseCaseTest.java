package com.franchise.seti.usecase;

import com.franchise.seti.model.Branch;
import com.franchise.seti.model.Product;
import com.franchise.seti.model.gateways.BranchRepository;
import com.franchise.seti.model.gateways.ProductRepository;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.FranchiseId;
import com.franchise.seti.model.ids.ProductId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseQueryUseCaseTest {

    @Test
    void getMaxStockProductPerBranch_shouldReturnMaxProductForEachBranch() {
        BranchRepository branchRepo = mock(BranchRepository.class);
        ProductRepository productRepo = mock(ProductRepository.class);

        FranchiseQueryUseCase useCase = new FranchiseQueryUseCase(branchRepo, productRepo);

        FranchiseId franchiseId = FranchiseId.of("f-1");

        Branch branchA = Branch.create(BranchId.of("b-a"), franchiseId, "Medellin");
        Branch branchB = Branch.create(BranchId.of("b-b"), franchiseId, "Bogota");

        when(branchRepo.findByFranchiseId(any(FranchiseId.class)))
                .thenReturn(Flux.just(branchA, branchB));

        // Products for branch A: choose stock 10
        Product a1 = Product.create(ProductId.of("p-a1"), branchA.getId(), "Coffee", 5);
        Product a2 = Product.create(ProductId.of("p-a2"), branchA.getId(), "Coffee Premium", 10);

        // Products for branch B: choose stock 7
        Product b1 = Product.create(ProductId.of("p-b1"), branchB.getId(), "Tea", 7);

        when(productRepo.findByBranchId(branchA.getId()))
                .thenReturn(Flux.just(a1, a2));

        when(productRepo.findByBranchId(branchB.getId()))
                .thenReturn(Flux.just(b1));

        StepVerifier.create(useCase.getMaxStockProductPerBranch(franchiseId.value()).collectList())
                .assertNext(list -> {
                    Assertions.assertEquals(2, list.size());

                    var rA = list.stream().filter(r -> r.branchId().equals("b-a")).findFirst().orElseThrow();
                    Assertions.assertEquals("Medellin", rA.branchName());
                    Assertions.assertEquals("p-a2", rA.productId());
                    Assertions.assertEquals("Coffee Premium", rA.productName());
                    Assertions.assertEquals(10, rA.stock());

                    var rB = list.stream().filter(r -> r.branchId().equals("b-b")).findFirst().orElseThrow();
                    Assertions.assertEquals("Bogota", rB.branchName());
                    Assertions.assertEquals("p-b1", rB.productId());
                    Assertions.assertEquals("Tea", rB.productName());
                    Assertions.assertEquals(7, rB.stock());
                })
                .verifyComplete();

        verify(branchRepo).findByFranchiseId(FranchiseId.of("f-1"));
        verify(productRepo).findByBranchId(branchA.getId());
        verify(productRepo).findByBranchId(branchB.getId());
        verifyNoMoreInteractions(branchRepo, productRepo);
    }
}


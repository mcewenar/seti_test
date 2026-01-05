package com.franchise.seti.usecase;

import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.gateways.BranchRepository;
import com.franchise.seti.model.gateways.ProductRepository;
import com.franchise.seti.model.ids.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Test
    void updateStock_whenStockNegative_shouldReturnValidation() {
        BranchRepository branchRepo = mock(BranchRepository.class);
        ProductRepository productRepo = mock(ProductRepository.class);

        ProductUseCase useCase = new ProductUseCase(branchRepo, productRepo);

        StepVerifier.create(useCase.updateStock("p-1", -1))
                .expectError(CustomExceptions.ValidationException.class)
                .verify();

        verifyNoInteractions(productRepo);
        verifyNoInteractions(branchRepo);
    }

    @Test
    void updateStock_whenProductMissing_shouldReturnNotFound() {
        BranchRepository branchRepo = mock(BranchRepository.class);
        ProductRepository productRepo = mock(ProductRepository.class);

        when(productRepo.findById(any(ProductId.class))).thenReturn(Mono.empty());

        ProductUseCase useCase = new ProductUseCase(branchRepo, productRepo);

        StepVerifier.create(useCase.updateStock("p-404", 10))
                .expectError(CustomExceptions.NotFoundException.class)
                .verify();

        verify(productRepo).findById(ProductId.of("p-404"));
        verify(productRepo, never()).save(any());
    }
}


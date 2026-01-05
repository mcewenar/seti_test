package com.franchise.seti.usecase;

import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.gateways.BranchRepository;
import com.franchise.seti.model.gateways.FranchiseRepository;
import com.franchise.seti.model.ids.FranchiseId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Test
    void addToFranchise_whenFranchiseMissing_shouldReturnNotFound() {
        FranchiseRepository franchiseRepo = mock(FranchiseRepository.class);
        BranchRepository branchRepo = mock(BranchRepository.class);

        when(franchiseRepo.findById(any(FranchiseId.class))).thenReturn(Mono.empty());

        BranchUseCase useCase = new BranchUseCase(franchiseRepo, branchRepo);

        StepVerifier.create(useCase.addToFranchise("f-404", "Medellin"))
                .expectError(CustomExceptions.NotFoundException.class)
                .verify();

        verify(franchiseRepo).findById(FranchiseId.of("f-404"));
        verifyNoInteractions(branchRepo);
    }
}


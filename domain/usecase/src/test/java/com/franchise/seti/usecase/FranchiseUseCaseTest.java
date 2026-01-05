package com.franchise.seti.usecase;

import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.gateways.FranchiseRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FranchiseUseCaseTest {

    @Test
    void create_whenNameExists_shouldReturnConflict() {
        FranchiseRepository repo = mock(FranchiseRepository.class);
        when(repo.existsByName(anyString())).thenReturn(Mono.just(true));

        FranchiseUseCase useCase = new FranchiseUseCase(repo);

        StepVerifier.create(useCase.create("Nequi"))
                .expectError(CustomExceptions.ConflictException.class)
                .verify();

        verify(repo).existsByName("Nequi");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void create_whenNameBlank_shouldReturnValidation() {
        FranchiseRepository repo = mock(FranchiseRepository.class);
        FranchiseUseCase useCase = new FranchiseUseCase(repo);

        StepVerifier.create(useCase.create("   "))
                .expectError(CustomExceptions.ValidationException.class)
                .verify();

        verifyNoInteractions(repo);
    }
}


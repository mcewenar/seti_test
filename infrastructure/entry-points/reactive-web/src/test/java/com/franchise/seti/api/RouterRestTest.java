package com.franchise.seti.api;

import com.franchise.seti.api.dto.CreateFranchiseRequest;
import com.franchise.seti.model.Franchise;
import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.ids.FranchiseId;
import com.franchise.seti.usecase.BranchUseCase;
import com.franchise.seti.usecase.FranchiseQueryUseCase;
import com.franchise.seti.usecase.FranchiseUseCase;
import com.franchise.seti.usecase.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//@ContextConfiguration(classes = {RouterRest.class, FranchiseHandler.class})
//@WebFluxTest
class RouterRestTest {

    private FranchiseUseCase franchiseUseCase;
    private BranchUseCase branchUseCase;
    private ProductUseCase productUseCase;
    private FranchiseQueryUseCase queryUseCase;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        franchiseUseCase = mock(FranchiseUseCase.class);
        branchUseCase = mock(BranchUseCase.class);
        productUseCase = mock(ProductUseCase.class);
        queryUseCase = mock(FranchiseQueryUseCase.class);

        // Real handler instance
        FranchiseHandler handler = new FranchiseHandler(franchiseUseCase, branchUseCase, productUseCase, queryUseCase);

        // Real filter (your exception mapping)
        ApiErrorFilter errorFilter = new ApiErrorFilter();

        // Real router
        RouterRest router = new RouterRest();
        var routes = router.routes(handler, errorFilter);

        // Bind WebTestClient directly to router (no server needed)
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void createFranchise_shouldReturn200AndBody() {
        var domain = Franchise.create(FranchiseId.of("f-1"), "Nequi");
        when(franchiseUseCase.create(anyString())).thenReturn(Mono.just(domain));

        client.post().uri("/franchises")
                .bodyValue(new CreateFranchiseRequest("Nequi"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("f-1")
                .jsonPath("$.name").isEqualTo("Nequi");

        verify(franchiseUseCase).create("Nequi");
        verifyNoMoreInteractions(franchiseUseCase);
    }

    @Test
    void createFranchise_whenValidationError_shouldReturn400() {
        when(franchiseUseCase.create(anyString()))
                .thenReturn(Mono.error(new CustomExceptions.ValidationException("franchise name is required")));

        client.post().uri("/franchises")
                .bodyValue(new CreateFranchiseRequest(""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("VALIDATION_ERROR")
                .jsonPath("$.message").isEqualTo("franchise name is required");
    }


}

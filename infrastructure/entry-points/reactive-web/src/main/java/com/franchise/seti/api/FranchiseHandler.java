package com.franchise.seti.api;

import com.franchise.seti.api.dto.*;
import com.franchise.seti.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@RequiredArgsConstructor
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;
    private final BranchUseCase branchUseCase;
    private final ProductUseCase productUseCase;
    private final FranchiseQueryUseCase queryUseCase;


    // POST /franchises
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                .flatMap(dto -> franchiseUseCase.create(dto.name()))
                .flatMap(f -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new FranchiseResponse(f.getId().value(), f.getName()))
                );
    }


    // POST /franchises/{franchiseId}/branches
    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(CreateBranchRequest.class)
                .flatMap(dto -> branchUseCase.addToFranchise(franchiseId, dto.name()))
                .flatMap(b -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new BranchResponse(b.getId().value(), b.getFranchiseId().value(), b.getName()))
                );
    }

    // POST /branches/{branchId}/products
    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String branchId = request.pathVariable("branchId");

        return request.bodyToMono(CreateProductRequest.class)
                .flatMap(dto -> productUseCase.addToBranch(branchId, dto.name(), dto.stock()))
                .flatMap(p -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ProductResponse(p.getId().value(), p.getBranchId().value(), p.getName(), p.getStock()))
                );
    }

    // DELETE /branches/{branchId}/products/{productId}
    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");

        return productUseCase.deleteFromBranch(branchId, productId)
                .then(noContent().build());
    }

    // PATCH /products/{productId}/stock
    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String productId = request.pathVariable("productId");

        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(dto -> productUseCase.updateStock(productId, dto.stock()))
                .flatMap(p -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ProductResponse(p.getId().value(), p.getBranchId().value(), p.getName(), p.getStock()))
                );
    }

    // GET /franchises/{franchiseId}/products/max-stock
    public Mono<ServerResponse> maxStockByBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(queryUseCase.getMaxStockProductPerBranch(franchiseId)
                                .map(r -> new MaxStockProductResponse(
                                        r.branchId(), r.branchName(), r.productId(), r.productName(), r.stock())),
                        MaxStockProductResponse.class
                );
    }
}


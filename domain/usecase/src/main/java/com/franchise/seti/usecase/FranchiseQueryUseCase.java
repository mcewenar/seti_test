package com.franchise.seti.usecase;



import com.franchise.seti.model.Branch;
import com.franchise.seti.model.Product;
import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.gateways.BranchRepository;
import com.franchise.seti.model.gateways.ProductRepository;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.FranchiseId;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Log
@RequiredArgsConstructor
public class FranchiseQueryUseCase {

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;


    /**
     * Required query:
     * Returns the product with the highest stock per branch for a given franchise.
     */
    public Flux<MaxStockProductByBranch> getMaxStockProductPerBranch(String franchiseId) {
        if (franchiseId == null || franchiseId.isBlank()) {
            return Flux.error(new CustomExceptions.ValidationException("franchiseId is required"));
        }

        FranchiseId fId = FranchiseId.of(franchiseId.trim());

        return branchRepository.findByFranchiseId(fId)
                .switchIfEmpty(Flux.empty())
                .flatMap(branch ->
                        maxProductForBranch(branch)
                                .switchIfEmpty(Mono.empty())
                )
                .doOnNext(r -> log.info(String.format(
                        "Max stock per branch computed franchiseId=%s branchId=%s productId=%s stock=%d",
                        fId.value(),
                        r.branchId(),
                        r.productId(),
                        r.stock()
                )))
                .doOnError(e -> log.severe(String.format(
                        "Error computing max stock per branch: %s",
                        e.getMessage()
                )))
                .doOnComplete(() -> log.info(String.format(
                        "Max stock query completed franchiseId=%s",
                        fId.value()
                )));
    }

    private Mono<MaxStockProductByBranch> maxProductForBranch(Branch branch) {
        BranchId bId = branch.getId();
        return productRepository.findByBranchId(bId)
                // Pick the product with max stock (reactive-friendly)
                .reduce((p1, p2) -> p1.getStock() >= p2.getStock() ? p1 : p2)
                .map(maxProduct -> toView(branch, maxProduct));
    }

    private MaxStockProductByBranch toView(Branch branch, Product product) {
        return new MaxStockProductByBranch(
                branch.getId().value(),
                branch.getName(),
                product.getId().value(),
                product.getName(),
                product.getStock()
        );
    }

    public record MaxStockProductByBranch(
            String branchId,
            String branchName,
            String productId,
            String productName,
            int stock
    ) {}
}


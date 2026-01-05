package com.franchise.seti.usecase;

import com.franchise.seti.model.Product;
import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.gateways.BranchRepository;
import com.franchise.seti.model.gateways.ProductRepository;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.ProductId;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log
@RequiredArgsConstructor
public class ProductUseCase {

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public Mono<Product> addToBranch(String branchId, String productName, int stock) {

        if (branchId == null || branchId.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("branchId is required"));
        }
        if (productName == null || productName.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("product name is required"));
        }
        if (stock < 0) {
            return Mono.error(new CustomExceptions.ValidationException("stock must be >= 0"));
        }

        BranchId bId = BranchId.of(branchId.trim());
        String normalizedName = productName.trim();

        return branchRepository.findById(bId)
                .switchIfEmpty(Mono.error(new CustomExceptions.NotFoundException(
                        "Branch not found id=" + bId.value()
                )))
                .flatMap(branch ->
                        productRepository.existsByNameAndBranchId(normalizedName, bId)
                                .flatMap(exists -> exists
                                                ? Mono.error(new CustomExceptions.ConflictException(
                                                "Product name already exists in this branch"
                                        ))
                                                : Mono.just(branch)
                                )
                )
                .map(branch -> Product.create(
                        ProductId.of(UUID.randomUUID().toString()),
                        bId,
                        normalizedName,
                        stock
                ))
                .flatMap(productRepository::save)
                .doOnNext(p ->
                        log.info(String.format(
                                "Product created id=%s branchId=%s name=%s stock=%d",
                                p.getId().value(),
                                p.getBranchId().value(),
                                p.getName(),
                                p.getStock()
                        ))
                )
                .doOnError(e ->
                        log.severe(String.format("Error adding product: %s", e.getMessage()))
                );
    }

    /** Required: Delete a product from a branch */
    public Mono<Void> deleteFromBranch(String branchId, String productId) {

        if (branchId == null || branchId.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("branchId is required"));
        }
        if (productId == null || productId.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("productId is required"));
        }

        BranchId bId = BranchId.of(branchId.trim());
        ProductId pId = ProductId.of(productId.trim());

        return productRepository.findById(pId)
                .switchIfEmpty(Mono.error(new CustomExceptions.NotFoundException(
                        "Product not found id=" + pId.value()
                )))
                .flatMap(product -> {
                    if (!product.getBranchId().value().equals(bId.value())) {
                        return Mono.error(new CustomExceptions.ConflictException(
                                "Product does not belong to the provided branchId"
                        ));
                    }
                    return productRepository.deleteById(pId);
                })
                .doOnSuccess(v ->
                        log.info(String.format(
                                "Product deleted id=%s branchId=%s",
                                pId.value(),
                                bId.value()
                        ))
                )
                .doOnError(e ->
                        log.severe(String.format("Error deleting product: %s", e.getMessage()))
                );
    }

    /** Required: Update the stock of a product */
    public Mono<Product> updateStock(String productId, int newStock) {

        if (productId == null || productId.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("productId is required"));
        }
        if (newStock < 0) {
            return Mono.error(new CustomExceptions.ValidationException("stock must be >= 0"));
        }

        ProductId pId = ProductId.of(productId.trim());

        return productRepository.findById(pId)
                .switchIfEmpty(Mono.error(new CustomExceptions.NotFoundException(
                        "Product not found id=" + pId.value()
                )))
                .map(product -> product.changeStock(newStock))
                .flatMap(productRepository::save)
                .doOnNext(p ->
                        log.info(String.format(
                                "Product stock updated id=%s newStock=%d",
                                p.getId().value(),
                                p.getStock()
                        ))
                )
                .doOnError(e ->
                        log.severe(String.format("Error updating stock: %s", e.getMessage()))
                );
    }

    /** Extra points: Rename product */
    public Mono<Product> rename(String productId, String newName) {

        if (productId == null || productId.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("productId is required"));
        }
        if (newName == null || newName.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("newName is required"));
        }

        ProductId pId = ProductId.of(productId.trim());
        String normalizedName = newName.trim();

        return productRepository.findById(pId)
                .switchIfEmpty(Mono.error(new CustomExceptions.NotFoundException(
                        "Product not found id=" + pId.value()
                )))
                .map(product -> product.rename(normalizedName))
                .flatMap(productRepository::save)
                .doOnNext(p ->
                        log.info(String.format(
                                "Product renamed id=%s name=%s",
                                p.getId().value(),
                                p.getName()
                        ))
                )
                .doOnError(e ->
                        log.severe(String.format("Error renaming product: %s", e.getMessage()))
                );
    }
}


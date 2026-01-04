package com.franchise.seti.mongo;

import com.franchise.seti.model.Product;
import com.franchise.seti.model.gateways.ProductRepository;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.ProductId;
import com.franchise.seti.mongo.entities.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MongoProductRepositoryAdapter implements ProductRepository {

    private final ProductMongoRepository repository;

    @Override
    public Mono<Product> save(Product product) {
        return repository.save(toDocument(product))
                .map(this::toDomain);
    }

    @Override
    public Mono<Product> findById(ProductId id) {
        return repository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Flux<Product> findByBranchId(BranchId branchId) {
        return repository.findByBranchId(branchId.value())
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(ProductId id) {
        return repository.deleteById(id.value());
    }

    @Override
    public Mono<Boolean> existsByNameAndBranchId(String name, BranchId branchId) {
        return repository.existsByNameAndBranchId(name, branchId.value());
    }

    private ProductDocument toDocument(Product p) {
        return ProductDocument.builder()
                .id(p.getId().value())
                .branchId(p.getBranchId().value())
                .name(p.getName())
                .stock(p.getStock())
                .build();
    }

    private Product toDomain(ProductDocument d) {
        return Product.create(
                ProductId.of(d.getId()),
                BranchId.of(d.getBranchId()),
                d.getName(),
                d.getStock()
        );
    }
}

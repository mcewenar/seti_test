package com.franchise.seti.usecase;

import com.franchise.seti.model.Franchise;
import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.gateways.FranchiseRepository;
import com.franchise.seti.model.ids.FranchiseId;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log
@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> create(String name) {
        if (name == null || name.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("franchise name is required"));
        }
        String normalizedName = name.trim();

        return franchiseRepository.existsByName(normalizedName)
                .flatMap(exists -> exists
                        ? Mono.error(new CustomExceptions.ConflictException("Franchise name already exists"))
                        : Mono.just(normalizedName)
                )
                .map(n -> Franchise.create(
                        FranchiseId.of(UUID.randomUUID().toString()),
                        n
                ))
                .flatMap(franchiseRepository::save)
                .doOnNext(f ->
                        log.info(String.format(
                                "Franchise created id=%s name=%s",
                                f.getId().value(),
                                f.getName()
                        ))
                )
                .doOnError(e ->
                        log.severe(String.format("Error creating franchise: %s", e.getMessage()))
                );
    }
}


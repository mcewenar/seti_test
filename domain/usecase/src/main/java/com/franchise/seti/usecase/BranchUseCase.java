package com.franchise.seti.usecase;

import com.franchise.seti.model.Branch;
import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.gateways.BranchRepository;
import com.franchise.seti.model.gateways.FranchiseRepository;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.FranchiseId;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log
@RequiredArgsConstructor
public class BranchUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public Mono<Branch> addToFranchise(String franchiseId, String branchName) {

        if (franchiseId == null || franchiseId.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("franchiseId is required"));
        }
        if (branchName == null || branchName.isBlank()) {
            return Mono.error(new CustomExceptions.ValidationException("branch name is required"));
        }

        FranchiseId fId = FranchiseId.of(franchiseId.trim());
        String normalizedBranchName = branchName.trim();

        return franchiseRepository.findById(fId)
                .switchIfEmpty(Mono.error(new CustomExceptions.NotFoundException(
                        "Franchise not found id=" + fId.value()
                )))
                // Optional: validate uniqueness of branch name inside same franchise
                .flatMap(franchise ->
                        branchRepository.existsByNameAndFranchiseId(normalizedBranchName, fId)
                                .flatMap(exists -> exists
                                                ? Mono.error(new CustomExceptions.ConflictException(
                                                "Branch name already exists in this franchise"
                                        ))
                                                : Mono.just(franchise)
                                )
                )
                .map(franchise -> Branch.create(
                        BranchId.of(UUID.randomUUID().toString()),
                        fId,
                        normalizedBranchName
                ))
                .flatMap(branchRepository::save)
                .doOnNext(b ->
                        log.info(String.format(
                                "Branch created id=%s franchiseId=%s name=%s",
                                b.getId().value(),
                                b.getFranchiseId().value(),
                                b.getName()
                        ))
                )
                .doOnError(e ->
                        log.severe(String.format("Error creating branch: %s", e.getMessage()))
                );
    }
}

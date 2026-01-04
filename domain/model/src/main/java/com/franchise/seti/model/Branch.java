package com.franchise.seti.model;

import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.FranchiseId;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Branch {
    BranchId id;
    FranchiseId franchiseId;
    String name;

    public static Branch create(BranchId id, FranchiseId franchiseId, String name) {
        if (franchiseId == null) {
            throw new CustomExceptions.NotFoundException("franchiseId is required");
        }
        validateName(name, "branchName");
        return Branch.builder()
                .id(id)
                .franchiseId(franchiseId)
                .name(name.trim())
                .build();
    }

    public Branch rename(String newName) {
        validateName(newName, "branchName");
        return this.toBuilder().name(newName.trim()).build();
    }

    private static void validateName(String name, String field) {
        if (name == null || name.isBlank()) {
            throw new CustomExceptions.NotFoundException(field + " is required");
        }
        if (name.trim().length() > 120) {
            throw new CustomExceptions.ValidationException(field + " must be <= 120 chars");
        }
    }
}


package com.franchise.seti.model;

import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.ids.FranchiseId;
import lombok.Builder;
import lombok.Value;


@Value
@Builder(toBuilder = true)
public class Franchise {
    FranchiseId id;
    String name;

    public static Franchise create(FranchiseId id, String name) {
        validateName(name, "franchiseName");
        return Franchise.builder()
                .id(id)
                .name(name.trim())
                .build();
    }

    public Franchise rename(String newName) {
        validateName(newName, "franchiseName");
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

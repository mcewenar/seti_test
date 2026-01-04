package com.franchise.seti.model.ids;

import com.franchise.seti.model.exception.CustomExceptions;

public record FranchiseId(String value) {
    public FranchiseId {
        if (value == null || value.isBlank()) throw new CustomExceptions.ValidationException("franchiseId is required");
    }
    public static FranchiseId of(String value) {
        return new FranchiseId(value);
    }
}

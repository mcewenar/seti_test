package com.franchise.seti.model.ids;

import com.franchise.seti.model.exception.CustomExceptions;

public record BranchId(String value) {
    public BranchId {
        if (value == null || value.isBlank()) throw new CustomExceptions.ValidationException("branchId is required");
    }
    public static BranchId of(String value) { return new BranchId(value); }
}

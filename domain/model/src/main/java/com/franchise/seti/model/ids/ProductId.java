package com.franchise.seti.model.ids;

import com.franchise.seti.model.exception.CustomExceptions;

public record ProductId(String value) {
    public ProductId {
        if (value == null || value.isBlank()) throw new CustomExceptions.ValidationException("productId is required");
    }
    public static ProductId of(String value) { return new ProductId(value); }
}

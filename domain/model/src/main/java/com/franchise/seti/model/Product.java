package com.franchise.seti.model;

import com.franchise.seti.model.exception.CustomExceptions;
import com.franchise.seti.model.ids.BranchId;
import com.franchise.seti.model.ids.ProductId;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Product {
    ProductId id;
    BranchId branchId;
    String name;
    int stock;

    public static Product create(ProductId id, BranchId branchId, String name, int stock) {
        if (branchId == null) {
            throw new CustomExceptions.NotFoundException("branchId is required");
        }
        validateName(name);
        validateStock(stock);
        return Product.builder()
                .id(id)
                .branchId(branchId)
                .name(name.trim())
                .stock(stock)
                .build();
    }

    public Product rename(String newName) {
        validateName(newName);
        return this.toBuilder().name(newName.trim()).build();
    }

    public Product changeStock(int newStock) {
        validateStock(newStock);
        return this.toBuilder().stock(newStock).build();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new CustomExceptions.NotFoundException("productName is required");
        }
        if (name.trim().length() > 120) {
            throw new CustomExceptions.ValidationException("productName must be <= 120 chars");
        }
    }

    private static void validateStock(int stock) {
        if (stock < 0) throw new CustomExceptions.ValidationException("stock must be >= 0");
    }
}


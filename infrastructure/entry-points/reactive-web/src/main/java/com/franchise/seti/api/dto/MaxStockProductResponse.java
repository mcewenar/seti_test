package com.franchise.seti.api.dto;

public record MaxStockProductResponse(
        String branchId,
        String branchName,
        String productId,
        String productName,
        int stock
) {}


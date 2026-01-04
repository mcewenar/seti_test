package com.franchise.seti.mongo.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product")
public class ProductDocument {
    @Id
    private String id;

    // FK to branch collection
    private String branchId;

    private String name;

    private int stock;
}

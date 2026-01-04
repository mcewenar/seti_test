package com.franchise.seti.mongo.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "branch")
public class BranchDocument {
    @Id
    private String id;

    // FK to franchise collection
    private String franchiseId;

    private String name;
}

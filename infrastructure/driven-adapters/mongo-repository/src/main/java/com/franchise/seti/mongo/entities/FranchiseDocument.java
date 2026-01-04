package com.franchise.seti.mongo.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franchise")
public class FranchiseDocument {

    @Id
    private String id;

    private String name;
}


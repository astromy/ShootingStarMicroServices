package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value="portfolio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Portfolio {
    @Id
    private ObjectId id;

    @NonNull
    private String studentId;
    @NonNull
    private String institutionCode;

}

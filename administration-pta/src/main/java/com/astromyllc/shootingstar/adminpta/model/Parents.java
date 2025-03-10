package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(value="parents")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Parents {
    @Id
    private ObjectId id;

    @NonNull
    private String firstNames;
    @NonNull
    private String lastName;
    @NonNull
    private String email;
    @NonNull
    private String contact1;
    private String contact2;
    @NonNull
    private String occupation;
    @NonNull
    private String placeOfWork;
    @NonNull
    private String parentType;
    @NonNull
    private String studentId;
    @NonNull
    private String institutionCode;
}

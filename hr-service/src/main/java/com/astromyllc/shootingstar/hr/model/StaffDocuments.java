package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(value = "staff_documents")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class StaffDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String document;
    private String documentType;
    private String documentExtension;
    private String staffDocs;
    private String institutionCode;
}

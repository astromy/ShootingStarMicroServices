package com.astromyllc.shootingstar.hr.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Entity
@Document(value = "staffPermissions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class StaffPermissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    private String staffCode;
    private String permissionCode;
    private String permission;
    private String institutionCode;
    private String state;
}

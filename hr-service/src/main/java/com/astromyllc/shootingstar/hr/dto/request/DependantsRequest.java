package com.astromyllc.shootingstar.hr.dto.request;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class DependantsRequest {
    private String id;
    private String name;
    private String dateOfBirth;
    private String relationType;
    private String gender;
    private String birthCertificate;
    private String institutionCode;

}

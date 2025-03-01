package com.astromyllc.shootingstar.academics.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReportInstitutionResponse {
        private Long id;
        private String name;
        private String slogan;
        private String country;
        private String region;
        private String city;
        private String email;
        private String website;
        private String contact1;
        private String contact2;
        private String status;
        private String bececode;
        private String postalAddress;
        private String classAverage;
        private String classGroup;

        @Lob // Marks this field as a Large Object (LOB)
        @Column(columnDefinition = "LONGTEXT")
        private String crest;
    }

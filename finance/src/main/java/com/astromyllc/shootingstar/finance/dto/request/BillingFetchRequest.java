package com.astromyllc.shootingstar.finance.dto.request;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class BillingFetchRequest {
    private String institutionCode;
    private String studentId;
    private String studentClass;
    private String academicYear;
    private String term;
}

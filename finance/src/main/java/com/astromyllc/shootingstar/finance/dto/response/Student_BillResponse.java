package com.astromyllc.shootingstar.finance.dto.response;

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
public class Student_BillResponse {
    private Long studentBillId;
    private String studentId;
    private String institutionCode;
    private String studentClass;
    private String term;
    private String academicYear;
    private Double amountDue;
    private Double amountPaid;
    private Double amountBalance;
    private Double oldBalance;
}
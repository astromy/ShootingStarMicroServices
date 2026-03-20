package com.astromyllc.shootingstar.adminpta.dto.response;

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
    private Double amountDue;
    private Double amountPaid;
    private String studentId;
    private Double amountBalance;
    private String term;
    private String studentClass;
    private Double oldBalance;
    private String institutionCode;
}

package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

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

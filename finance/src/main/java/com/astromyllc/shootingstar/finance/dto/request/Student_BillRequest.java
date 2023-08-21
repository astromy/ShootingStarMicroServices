package com.astromyllc.shootingstar.finance.dto.request;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Student_BillRequest {
    private Long studentBillId;
    @Nonnull
    private Double amountDue;
    @Nonnull
    private Double amountPaid;
    @Nonnull
    private String studentId;
    private Double amountBalance;
    @Nonnull
    private String term;
    @Nonnull
    private String studentClass;
    private Double oldBalance;
    @Nonnull
    private String institutionCode;
}

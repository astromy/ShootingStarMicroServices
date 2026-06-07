package com.astromyllc.shootingstar.finance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryRequest {
    private Long salaryId;
    private String staffId;
    private String institutionCode;
    private String staffName;
    private String designation;
    private String academicYear;
    private String payPeriod;
    private Double basicSalary;
    private String paymentMethod;
    private String externalReference;
    private List<SalaryItemRequest> salaryItems;
}
package com.astromyllc.shootingstar.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryResponse {
    private Long salaryId;
    private String staffId;
    private String institutionCode;
    private String staffName;
    private String designation;
    private String academicYear;
    private String payPeriod;
    private Double basicSalary;
    private Double totalAllowances;
    private Double totalDeductions;
    private Double netSalary;
    private String status;
    private LocalDate paymentDate;
    private LocalDateTime createdAt;
    private String processedBy;
    private String paymentMethod;
    private String externalReference;
    private String createdBy;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
    private Double grossPay;
    private Double taxableIncome;
    private Double employeeSsnit;
    private Double employerSsnit;
    private Double incomeTax;
    private String bankName;
    private String bankBranch;
    private String accountName;
    private String accountNumber;
    private String momoNumber;
    private List<SalaryItemResponse> salaryItems;
}
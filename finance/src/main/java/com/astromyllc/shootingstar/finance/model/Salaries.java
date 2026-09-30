package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A single payroll run for one staff member for one pay period.
 * SalaryItems are the allowance/deduction line items within the run.
 */
@Entity
@Table(name = "salary")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "salaryId")
public class Salaries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryId;

    @Column(nullable = false)
    private String staffId;

    @Column(nullable = false)
    private String institutionCode;

    private String staffName;
    private String designation;

    /**
     * e.g. "2024/2025"
     */
    private String academicYear;

    /**
     * e.g. "January 2025" or "1st Term 2025"
     */
    private String payPeriod;

    private Double basicSalary;
    private Double totalAllowances;
    private Double totalDeductions;
    private Double netSalary;

    /**
     * PENDING | APPROVED | PAID
     */
    private String status;

    private LocalDate paymentDate;
    private LocalDateTime createdAt;
    private String processedBy;
    private String paymentMethod;
    private String externalReference;

    // Who created and approved the run - taken from the login by the gateway.
    // processedBy (above) records who marked it paid.
    private String createdBy;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;

    // Statutory figures, calculated when the run is created (PayrollCalculator).
    private Double grossPay;
    private Double taxableIncome;
    private Double employeeSsnit;
    // The school's own SSNIT contribution - shown on the payslip, not deducted.
    private Double employerSsnit;
    private Double incomeTax;

    // Payment details copied from the staff member's salary profile at run time,
    // so the payslip shows where that month's salary was paid.
    private String bankName;
    private String bankBranch;
    private String accountName;
    private String accountNumber;
    private String momoNumber;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "salaryId")
    private List<SalaryItem> salaryItems;
}
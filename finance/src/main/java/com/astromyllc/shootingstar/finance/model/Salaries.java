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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "salaryId")
    private List<SalaryItem> salaryItems;
}
package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A staff member's standing pay record: basic salary, regular allowances and
 * deductions, and where they're paid. Entered once, edited when something
 * changes; each payroll run is generated from it. Editing a profile only
 * affects runs created afterwards.
 *
 * staffId always refers to an HR staff record - the gateway checks it against
 * HR before saving, and copies staffName / designation from HR.
 */
@Entity
@Table(name = "salary_profile",
        uniqueConstraints = @UniqueConstraint(name = "uk_salary_profile_staff", columnNames = {"institutionCode", "staffId"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "salaryProfileId")
public class SalaryProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryProfileId;

    @Column(nullable = false)
    private String institutionCode;

    @Column(nullable = false)
    private String staffId;

    private String staffName;
    private String designation;

    private Double basicSalary;

    /**
     * BANK | MOMO | CASH
     */
    private String paymentMethod;
    private String bankName;
    private String bankBranch;
    private String accountName;
    private String accountNumber;
    private String momoNumber;

    /**
     * Inactive profiles are left out of payroll runs (e.g. staff on unpaid leave).
     */
    private Boolean active;

    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * Regular allowances and deductions. SSNIT and income tax are not stored
     * here - they're calculated on each run.
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "salaryProfileId")
    private List<SalaryItem> items = new ArrayList<>();
}

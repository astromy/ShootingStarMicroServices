package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * One line-item (allowance or deduction) within a salary run.
 * Examples: Housing Allowance +500, SSNIT -13.5%, Income Tax -200
 */
@Entity
@Table(name = "salaryitem")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "salaryItemId")
public class SalaryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryItemId;

    @Column(nullable = false)
    private String itemName;

    /**
     * ALLOWANCE | DEDUCTION
     */
    private String itemType;

    private Double amount;

    /**
     * If true, amount = percentage * basicSalary
     */
    private Boolean isPercentage;
    private Double percentageRate;
}
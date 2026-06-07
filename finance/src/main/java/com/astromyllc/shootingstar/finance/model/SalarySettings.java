package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * @author Peter Ackon
 * Institution-level payroll template.
 * Defines standard allowances and deductions that apply to all staff.
 * Individual salary runs are seeded from these settings, then adjusted per staff.
 */
@Entity
@Table(name = "salarysettings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "salarySettingsId")
public class SalarySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salarySettingsId;

    @Column(nullable = false)
    private String institutionCode;

    /**
     * % of basic for SSNIT employee contribution
     */
    private Double ssnitEmployeeRate;

    /**
     * % of basic for SSNIT employer contribution
     */
    private Double ssnitEmployerRate;

    /**
     * Income tax tiers — stored as JSON string for flexibility
     */
    @Column(columnDefinition = "TEXT")
    private String incomeTaxBands;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "salarySettingsId")
    private List<SalaryItem> defaultAllowances;
}
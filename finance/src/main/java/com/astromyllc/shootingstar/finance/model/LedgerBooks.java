package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A ledger account (chart-of-accounts entry) for an institution.
 *
 * Field names match the contract already consumed by
 * astro-orb/_financeLedgers.js (ledgerCreate, ledgerFetchAll, renderTable):
 *
 *   ledgerBookId, accountName, accountCode, accountType, description, currentBalance
 *
 * accountType: ASSET | LIABILITY | INCOME | EXPENSE | EQUITY
 *
 * Replaces the previously empty stub.
 */
@Entity
@Table(name = "ledger_books",
       uniqueConstraints = @UniqueConstraint(
               name = "uq_ledger_book",
               columnNames = {"institutionCode", "accountName"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "ledgerBookId")
public class LedgerBooks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerBookId;

    @Column(nullable = false)
    private String institutionCode;

    @Column(nullable = false)
    private String accountName;       // e.g. "Stores & Prospectus Income", "Cash at Hand"

    private String accountCode;       // e.g. "4002" — optional chart-of-accounts code

    /** ASSET | LIABILITY | INCOME | EXPENSE | EQUITY */
    @Column(nullable = false)
    private String accountType;

    private String description;

    /**
     * Running balance for this account.
     * INCOME/LIABILITY/EQUITY: CREDIT increases, DEBIT decreases.
     * ASSET/EXPENSE: DEBIT increases, CREDIT decreases.
     */
    private Double currentBalance;

    private Boolean active;

    private LocalDateTime createdAt;
}

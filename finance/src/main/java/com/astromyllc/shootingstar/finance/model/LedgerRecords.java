package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A single journal entry (debit or credit) posted against a LedgerBooks account.
 *
 * Field names match the contract consumed by
 * astro-orb/subscripts/financeLedger.js (openJournalModal → ledgerFetchRecords):
 *
 *   entryType, amount, description, sourceReference, sourceType, entryDate
 *
 * entryType:  DEBIT | CREDIT
 * sourceType: STORE_SALE | STUDENT_PAYMENT | SALARY | MANUAL
 *
 * Replaces the previously empty stub.
 */
@Entity
@Table(name = "ledger_records")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "ledgerRecordId")
public class LedgerRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerRecordId;

    @Column(nullable = false)
    private String institutionCode;

    @Column(nullable = false)
    private Long ledgerBookId;

    /** DEBIT | CREDIT */
    @Column(nullable = false)
    private String entryType;

    @Column(nullable = false)
    private Double amount;

    private String description;

    /** STORE_SALE | STUDENT_PAYMENT | SALARY | MANUAL */
    private String sourceType;

    /** orderRef, BP-<id>, SAL-<id>, etc. — used for idempotency checks */
    private String sourceReference;

    /** External payment reference: Paystack ref, MoMo id, etc. */
    private String externalReference;

    private String postedBy;

    private LocalDateTime entryDate;
    private LocalDateTime postedAt;
}

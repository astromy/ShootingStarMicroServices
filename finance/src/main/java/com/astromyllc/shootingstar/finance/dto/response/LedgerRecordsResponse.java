package com.astromyllc.shootingstar.finance.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Response shape matching the rows rendered by
 * financeLedger.js → openJournalModal() (r.entryDate, r.entryType,
 * r.amount, r.description, r.sourceReference, r.sourceType).
 */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class LedgerRecordsResponse {
    private Long ledgerRecordId;
    private String institutionCode;
    private Long ledgerBookId;
    private String entryType;        // DEBIT | CREDIT
    private Double amount;
    private String description;
    private String sourceType;       // STORE_SALE | STUDENT_PAYMENT | SALARY | MANUAL
    private String sourceReference;
    private String externalReference;
    private String postedBy;
    private LocalDateTime entryDate;
    private LocalDateTime postedAt;
}

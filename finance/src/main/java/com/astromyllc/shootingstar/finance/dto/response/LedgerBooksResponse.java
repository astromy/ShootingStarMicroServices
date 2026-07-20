package com.astromyllc.shootingstar.finance.dto.response;

import lombok.*;

/**
 * Response shape matching window.ledgerState.allAccounts items
 * consumed by _financeLedgers.js / financeLedger.js renderTable().
 */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class LedgerBooksResponse {
    private Long ledgerBookId;
    private String institutionCode;
    private String accountName;
    private String accountCode;
    private String accountType;     // ASSET | LIABILITY | INCOME | EXPENSE | EQUITY
    private String description;
    private Double currentBalance;
}

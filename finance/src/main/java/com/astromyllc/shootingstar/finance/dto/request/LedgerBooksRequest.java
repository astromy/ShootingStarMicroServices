package com.astromyllc.shootingstar.finance.dto.request;

import lombok.*;

/**
 * Payload for POST /api/finance/ledger/create
 * Matches window.ledgerCreate(req) in _financeLedgers.js — used for both
 * creating a new account (ledgerBookId = null) and editing an existing one.
 */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class LedgerBooksRequest {
    private Long ledgerBookId;        // null = create new
    private String institutionCode;
    private String accountName;
    private String accountCode;
    private String accountType;       // ASSET | LIABILITY | INCOME | EXPENSE | EQUITY
    private String description;
}

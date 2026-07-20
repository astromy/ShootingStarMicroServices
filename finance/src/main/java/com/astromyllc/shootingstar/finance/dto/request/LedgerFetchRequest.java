package com.astromyllc.shootingstar.finance.dto.request;

import lombok.*;

/**
 * Payload for POST /api/finance/ledger/get-by-institution
 * Matches window.ledgerLoad() / ledgerFetchAll() / ledgerFetchByType()
 * in _financeLedgers.js.
 *
 * academicYear / term are accepted for forward-compatibility but are
 * currently unused by LedgerBooks (balances are running totals, not
 * period-scoped). Pass null from the frontend.
 */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class LedgerFetchRequest {
    private String institutionCode;
    private String accountType;   // optional filter: ASSET | LIABILITY | INCOME | EXPENSE | EQUITY | null
    private String academicYear;  // reserved
    private String term;          // reserved
}

package com.astromyllc.shootingstar.finance.dto.request;

import lombok.*;

/**
 * Payload for POST /api/finance/ledger/get-records
 * Matches window.ledgerFetchRecords(bookId, year, term) in _financeLedgers.js.
 *
 * academicYear / term are accepted for forward-compatibility (e.g. to scope
 * journal entries to a term) but are currently unused; pass null to fetch
 * the full history for the account.
 */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class LedgerRecordsFetchRequest {
    private String institutionCode;
    private Long ledgerBookId;
    private String academicYear;  // reserved
    private String term;          // reserved
}

package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.*;
import com.astromyllc.shootingstar.finance.dto.response.LedgerBooksResponse;
import com.astromyllc.shootingstar.finance.dto.response.LedgerRecordsResponse;
import com.astromyllc.shootingstar.finance.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Backs the four routes already proxied by astro-orb FinanceController:
 *   POST ledger/create               → /api/finance/ledger/create
 *   POST ledger/get-by-institution    → /api/finance/ledger/get-by-institution
 *   POST ledger/get-records           → /api/finance/ledger/get-records
 *   (trial-balance / income-statement are computed client-side from
 *    get-by-institution and have no dedicated backend endpoint)
 *
 * Plus the new internal endpoint used by stores-inventory:
 *   POST store-sale → /api/finance/ledger/store-sale
 */
@RestController
@RequestMapping("/api/finance/ledger")
@RequiredArgsConstructor
@Slf4j
public class LedgerController {

    private final LedgerService ledgerService;

    /** Create or edit a chart-of-accounts entry — window.ledgerCreate(req) */
    @PostMapping("/create")
    public LedgerBooksResponse createOrUpdate(@RequestBody LedgerBooksRequest request) {
        log.info("Ledger account upsert: institution={} account={}",
                request.getInstitutionCode(), request.getAccountName());
        return ledgerService.createOrUpdateAccount(request);
    }

    /** Chart of accounts, optional type filter — window.ledgerLoad / ledgerFetchByType */
    @PostMapping("/get-by-institution")
    public List<LedgerBooksResponse> getByInstitution(@RequestBody LedgerFetchRequest request) {
        return ledgerService.getAccountsByInstitution(request);
    }

    /** Journal entries for one account — window.ledgerFetchRecords(bookId, ...) */
    @PostMapping("/get-records")
    public List<LedgerRecordsResponse> getRecords(@RequestBody LedgerRecordsFetchRequest request) {
        return ledgerService.getRecordsByAccount(request);
    }

    // ── Internal: called by Stores-Inventory service ──────────────────────────

    /**
     * POST /api/finance/ledger/store-sale
     * Invoked automatically by stores-inventory's FinanceLedgerClient
     * when a StoreOrder is fulfilled. Returns a ledgerReference the
     * calling service stores against the order.
     */
    @PostMapping("/store-sale")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> postStoreSale(@RequestBody StoreSaleLedgerRequest request) {
        log.info("Store-sale ledger post: institution={} order={} amount={}",
                request.getInstitutionCode(), request.getOrderRef(), request.getAmount());
        LedgerRecordsResponse record = ledgerService.postStoreSale(request);
        return Map.of(
                "ledgerReference", "LR-" + record.getLedgerRecordId(),
                "ledgerRecordId", record.getLedgerRecordId(),
                "ledgerBookId", record.getLedgerBookId(),
                "postedAt", record.getPostedAt().toString()
        );
    }
}

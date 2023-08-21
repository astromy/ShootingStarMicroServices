package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Financial_BooksRequest;
import com.astromyllc.shootingstar.finance.dto.response.Financial_BooksResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.Financial_BooksServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FinancialBookController {

    private final Financial_BooksServiceInterface financialBooksServiceInterface;

    @PostMapping
    @RequestMapping("/api/finance/create-Ledger")
    @ResponseStatus(HttpStatus.CREATED)
    public Financial_BooksResponse createFinancialBook(@RequestBody Financial_BooksRequest billRequest) {
        log.info("Application  Received");
        return financialBooksServiceInterface.createLedger(billRequest);
    }

    @PostMapping
    @RequestMapping("/api/finance/create-LedgerList")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Financial_BooksResponse> createFinancialBooks(@RequestBody List<Financial_BooksRequest> billRequest) {
        log.info("Application  Received");
        return financialBooksServiceInterface.createLedgerList(billRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/get-Ledger-by-institution")
    @ResponseStatus(HttpStatus.OK)
    public List<Financial_BooksResponse> getFinancialBooksByInstitution(@RequestBody BillFetchRequest billFetchRequest) {
        log.info("Application  Received");
        return financialBooksServiceInterface.fetchLedgerByInstitution(billFetchRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/get-Ledger-by-institutionAndName")
    @ResponseStatus(HttpStatus.OK)
    public Financial_BooksResponse getFinancialBookByInstitution(@RequestBody BillFetchRequest billFetchRequest) {
        log.info("Application  Received");
        return financialBooksServiceInterface.fetchLedgerByInstitutionAndName(billFetchRequest);
    }

}

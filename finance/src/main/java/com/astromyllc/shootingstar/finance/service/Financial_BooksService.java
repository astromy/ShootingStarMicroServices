package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Financial_BooksRequest;
import com.astromyllc.shootingstar.finance.dto.response.Financial_BooksResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.Financial_BooksServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class Financial_BooksService implements Financial_BooksServiceInterface {
    @Override
    public Financial_BooksResponse createLedger(Financial_BooksRequest billRequest) {
        return null;
    }

    @Override
    public List<Financial_BooksResponse> createLedgerList(List<Financial_BooksRequest> billRequest) {
        return null;
    }

    @Override
    public List<Financial_BooksResponse> fetchLedgerByInstitution(BillFetchRequest billFetchRequest) {
        return null;
    }

    @Override
    public Financial_BooksResponse fetchLedgerByInstitutionAndName(BillFetchRequest billFetchRequest) {
        return null;
    }
}

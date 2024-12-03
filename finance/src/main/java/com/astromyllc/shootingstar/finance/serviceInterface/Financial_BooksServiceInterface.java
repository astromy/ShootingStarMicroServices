package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Financial_BooksRequest;
import com.astromyllc.shootingstar.finance.dto.response.Financial_BooksResponse;

import java.util.List;

public interface Financial_BooksServiceInterface {
    public Financial_BooksResponse createLedger(Financial_BooksRequest billRequest);

    public List<Financial_BooksResponse> createLedgerList(List<Financial_BooksRequest> billRequest);

    public List<Financial_BooksResponse> fetchLedgerByInstitution(BillFetchRequest billFetchRequest);

    public Financial_BooksResponse fetchLedgerByInstitutionAndName(BillFetchRequest billFetchRequest);
}

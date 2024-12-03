package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillResponse;

import java.util.List;
import java.util.Optional;

public interface BillServiceInterface {

    public Optional<BillResponse> createBill(BillRequest billRequest);

    public Optional<List<BillResponse>> createBills(List<BillRequest> billRequest);

    public Optional<List<BillResponse>> fetchBillsByInstitution(SingleStringRequest singleStringRequest);

    public Optional<BillResponse> fetchBillByInstitutionAndName(BillFetchRequest billFetchRequest);
}

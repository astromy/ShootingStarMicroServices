package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillResponse;

import java.util.List;

public interface BillServiceInterface {

    public BillResponse createBill(BillRequest billRequest);

    public List<BillResponse> createBills(List<BillRequest> billRequest);

    public List<BillResponse> fetchBillsByInstitution(BillFetchRequest billFetchRequest);

    public BillResponse fetchBillByInstitutionAndName(BillFetchRequest billFetchRequest);
}

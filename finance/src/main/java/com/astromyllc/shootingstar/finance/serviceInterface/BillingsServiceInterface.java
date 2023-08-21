package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;

import java.util.List;

public interface BillingsServiceInterface {
    public BillingsResponse createBilling(BillingsRequest billingsRequest);

    public List<BillingsResponse> createBillings(List<BillingsRequest> billingsRequest);

    public List<BillingsResponse> fetchBillingsByInstitution(BillFetchRequest billingFetchRequest);

    public List<BillingsResponse> fetchBillingByInstitutionAndStudent(BillingFetchRequest billingFetchRequest);

    public List<BillingsResponse> fetchBillingByInstitutionStudentClassTerm(BillingFetchRequest billingFetchRequest);

    public List<BillingsResponse> fetchClassBillingByInstitution(BillingFetchRequest billingFetchRequest);

    public List<BillingsResponse> fetchSchoolBillingByInstitution(BillingFetchRequest billingFetchRequest);
}

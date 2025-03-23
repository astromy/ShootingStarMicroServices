package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.BillingFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;

import java.util.List;
import java.util.Optional;

public interface BillingsServiceInterface {
    public BillingsResponse updateBilling(BillingsRequest billingsRequest);

    public Optional<List<Student_BillResponse>> createBillings(BillingsRequest billingsRequest);

    public Optional<List<BillingsResponse>> fetchBillingsByInstitution(BillingFetchRequest billingFetchRequest);

    public Optional<List<BillingsResponse>> fetchBillingByInstitutionAndStudent(BillingFetchRequest billingFetchRequest);

    public Optional<List<BillingsResponse>> fetchBillingByInstitutionStudentClassTerm(BillingFetchRequest billingFetchRequest);

    public Optional<List<BillingsResponse>> fetchClassBillingByInstitution(BillingFetchRequest billingFetchRequest);

    public Optional<List<BillingsResponse>> fetchSchoolBillingByInstitution(BillingFetchRequest billingFetchRequest);
}

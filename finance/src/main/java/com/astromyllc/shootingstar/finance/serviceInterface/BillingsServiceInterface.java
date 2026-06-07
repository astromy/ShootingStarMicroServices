package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.BillingFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;

import java.util.List;
import java.util.Optional;

public interface BillingsServiceInterface {

    BillingsResponse updateBilling(BillingsRequest billingsRequest);

    Optional<List<Student_BillResponse>> createBillings(BillingsRequest billingsRequest);

    Optional<List<BillingsResponse>> fetchBillingsByInstitution(BillingFetchRequest billingFetchRequest);

    Optional<List<BillingsResponse>> fetchBillingByInstitutionAndStudent(BillingFetchRequest billingFetchRequest);

    Optional<List<BillingsResponse>> fetchBillingByInstitutionStudentClassTerm(BillingFetchRequest billingFetchRequest);

    Optional<List<BillingsResponse>> fetchClassBillingByInstitution(BillingFetchRequest billingFetchRequest);

    Optional<List<BillingsResponse>> fetchSchoolBillingByInstitution(BillingFetchRequest billingFetchRequest);

    Optional<List<BillingsResponse>> fetchBillingByInstitutionClass(BillingFetchRequest billingFetchRequest);
}
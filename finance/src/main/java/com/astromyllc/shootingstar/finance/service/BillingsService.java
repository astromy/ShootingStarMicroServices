package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.repositoy.BillingsRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.BillingsServiceInterface;
import com.astromyllc.shootingstar.finance.utils.BillingsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillingsService implements BillingsServiceInterface {
    private final BillingsRepository billingsRepository;
    private final BillingsUtil billingsUtil;

    @Override
    public BillingsResponse createBilling(BillingsRequest billingsRequest) {
        Optional<Billings> billings = billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equals(billingsRequest.getInstitutionCode())
                        && b.getBillname().equals(billingsRequest.getBillname())).findFirst();
        if (billings.isEmpty()) {
            Billings billings1 = billingsUtil.mapBillingRequest_ToBilling(billingsRequest);
            billingsRepository.save(billings1);
            billingsUtil.billingGlobalList.add(billings1);
            return billingsUtil.mapBillings_ToBillingResponse(billings1);
        } else {
            billingsRepository.save(billingsUtil.mapBillingRequest_ToBilling(billingsRequest, billings.get()));
        }
        return null;
    }

    @Override
    public List<BillingsResponse> createBillings(List<BillingsRequest> billingsRequest) {
        return null;
    }

    @Override
    public List<BillingsResponse> fetchBillingsByInstitution(BillFetchRequest billFetchRequest) {
        return billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equals(billFetchRequest.getInstitutionCode()))
                .map(billingsUtil::mapBillings_ToBillingResponse).collect(Collectors.toList());
    }

    @Override
    public List<BillingsResponse> fetchBillingByInstitutionAndStudent(BillingFetchRequest billingFetchRequest) {
        return billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equals(billingFetchRequest.getInstitutionCode())
                        && b.getStudentId().equals(billingFetchRequest.getStudentId()))
                .map(billingsUtil::mapBillings_ToBillingResponse).collect(Collectors.toList());
    }

    @Override
    public List<BillingsResponse> fetchBillingByInstitutionStudentClassTerm(BillingFetchRequest billingFetchRequest) {
        return billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equals(billingFetchRequest.getInstitutionCode())
                        && b.getStudentId().equals(billingFetchRequest.getStudentId())
                        && b.getStudentClass().equals(billingFetchRequest.getStudentClass())
                        && b.getTerm().equals(billingFetchRequest.getTerm()))
                .map(billingsUtil::mapBillings_ToBillingResponse).collect(Collectors.toList());
    }

    @Override
    public List<BillingsResponse> fetchClassBillingByInstitution(BillingFetchRequest billingFetchRequest) {
        return billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equals(billingFetchRequest.getInstitutionCode())
                        && b.getStudentClass().equals(billingFetchRequest.getStudentClass())
                        && b.getTerm().equals(billingFetchRequest.getTerm()))
                .map(br -> billingsUtil.mapBillings_ToBillingResponse(br)).collect(Collectors.toList());
    }

    @Override
    public List<BillingsResponse> fetchSchoolBillingByInstitution(BillingFetchRequest billingFetchRequest) {
        return billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equals(billingFetchRequest.getInstitutionCode())
                        && b.getTerm().equals(billingFetchRequest.getTerm()))
                .map(br -> billingsUtil.mapBillings_ToBillingResponse(br)).collect(Collectors.toList());
    }
}

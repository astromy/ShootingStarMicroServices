package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.model.Bill;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.repositoy.BillingsRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.BillingsServiceInterface;
import com.astromyllc.shootingstar.finance.utils.BillUtil;
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
    private final BillUtil billsUtil;

    @Override
    public BillingsResponse updateBilling(BillingsRequest billingsRequest) {

        Optional<Billings> billings = billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingsRequest.getInstitutionCode())
                        && b.getBillname().equalsIgnoreCase(billingsRequest.getBillname().get(0))).findFirst();
        if (billings.isEmpty()) {

            /*Billings billings1 = billingsUtil.mapBillingRequest_ToBilling(inst, studClass, term, amnt, billBal, billDesc, billName, student);
            billingsRepository.save(billings1);
            billingsUtil.billingGlobalList.add(billings1);
            return billingsUtil.mapBillings_ToBillingResponse(billings1);*/
        } else {
          //Bill=  billsUtil.billGlobalList.stream().filter(b->)
           // double bal=(billings.getBillamnt()+billings.getBillamntbal())-(billings.getBillamntbal()+billingsRequest.)
            billingsRepository.save(billingsUtil.mapBillingRequest_ToBilling(billingsRequest, billings.get()));
        }
        return null;
    }

    @Override
    public Optional<List<BillingsResponse>> createBillings(BillingsRequest billingsRequest) {

     return  Optional.ofNullable(billingsRequest.getStudentId().stream()
                .flatMap(stud -> billsUtil.billGlobalList.stream()
                        .filter(bill -> billingsRequest.getInstitutionCode().equalsIgnoreCase(bill.getInstitutionCode()) &&
                                billingsRequest.getBillname().stream()
                                        .anyMatch(request -> request.equalsIgnoreCase(bill.getBill_Name()))
                        )
                        .map(bill -> billingsUtil.mapBillingRequest_ToBilling(
                                billingsRequest.getInstitutionCode(),
                                billingsRequest.getStudentClass(),
                                billingsRequest.getTerm(),
                                bill.getBill_Amount(),
                                bill.getBill_Amount(),
                                bill.getBill_Description(),
                                bill.getBill_Name(),
                                stud)
                        )
                ).map(b->billingsUtil.mapBillings_ToBillingResponse(b))
                .collect(Collectors.toList()));

    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingsByInstitution(BillingFetchRequest billFetchRequest) {
        return Optional.ofNullable(billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billFetchRequest.getInstitutionCode()))
                .map(billingsUtil::mapBillings_ToBillingResponse).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingByInstitutionAndStudent(BillingFetchRequest billingFetchRequest) {
        return Optional.ofNullable(billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingFetchRequest.getInstitutionCode())
                        && b.getStudentId().equalsIgnoreCase(billingFetchRequest.getStudentId()))
                .map(billingsUtil::mapBillings_ToBillingResponse).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingByInstitutionStudentClassTerm(BillingFetchRequest billingFetchRequest) {
        return Optional.ofNullable(billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingFetchRequest.getInstitutionCode())
                        && b.getStudentId().equalsIgnoreCase(billingFetchRequest.getStudentId())
                        && b.getStudentClass().equalsIgnoreCase(billingFetchRequest.getStudentClass())
                        && b.getTerm().equalsIgnoreCase(billingFetchRequest.getTerm()))
                .map(billingsUtil::mapBillings_ToBillingResponse).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchClassBillingByInstitution(BillingFetchRequest billingFetchRequest) {
        return Optional.ofNullable(billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingFetchRequest.getInstitutionCode())
                        && b.getStudentClass().equalsIgnoreCase(billingFetchRequest.getStudentClass())
                        && b.getTerm().equalsIgnoreCase(billingFetchRequest.getTerm()))
                .map(br -> billingsUtil.mapBillings_ToBillingResponse(br)).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchSchoolBillingByInstitution(BillingFetchRequest billingFetchRequest) {
        return Optional.ofNullable(billingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingFetchRequest.getInstitutionCode())
                        && b.getTerm().equalsIgnoreCase(billingFetchRequest.getTerm()))
                .map(br -> billingsUtil.mapBillings_ToBillingResponse(br)).collect(Collectors.toList()));
    }
}

package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillResponse;
import com.astromyllc.shootingstar.finance.model.Bill;
import com.astromyllc.shootingstar.finance.repositoy.BillRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.BillServiceInterface;
import com.astromyllc.shootingstar.finance.utils.BillUtil;
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
public class BillService implements BillServiceInterface {

    private final BillRepository billRepository;
    private final BillUtil billUtil;
    @Override
    public Optional<BillResponse> createBill(BillRequest billRequest) {
        Optional<Bill> bill= BillUtil.billGlobalList.stream().filter(b->b.getInstitutionCode().equalsIgnoreCase(billRequest.getInstitutionCode())).findFirst();
        if(bill.isEmpty()){
            Bill bill1=billUtil.mapBillRequest_ToBill(billRequest);
            billRepository.save(bill1);
            BillUtil.billGlobalList.add(bill1);
            return Optional.ofNullable(billUtil.mapBill_ToBillResponse(bill1));
        }else{
            billRepository.save(billUtil.mapBillRequest_ToBill(billRequest,bill.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<BillResponse>> createBills(List<BillRequest> billRequests) {

        List<Bill> updatedBills = billRequests.stream()
                .map(billRequest -> {
                    Optional<Bill> existingBillOpt = BillUtil.billGlobalList.stream()
                            .filter(bill -> bill.getBill_Name().equalsIgnoreCase(billRequest.getBill_Name())
                                    && bill.getInstitutionCode().equalsIgnoreCase(billRequest.getInstitutionCode()))
                            .findFirst();

                    return existingBillOpt.map(existingBill -> {
                        billUtil.mapBillRequest_ToBill(billRequest,existingBill);
                        return existingBill; // Return the updated existing bill
                    }).orElseGet(() ->{
                        Bill nb= billUtil.mapBillRequest_ToBill(billRequest);
                        BillUtil.billGlobalList.add(nb);
                        return nb;
                    }); // Create new if not exists
                })
                .toList();

        billRepository.saveAll(updatedBills);

        // Map the updated bills to BillResponse
        List<BillResponse> billResponses = BillUtil.billGlobalList.stream()
                .filter(b-> b.getInstitutionCode().equalsIgnoreCase(billRequests.get(0).getInstitutionCode()))
                .map(billUtil::mapBill_ToBillResponse) // Assuming a method to map Bill to BillResponse
                .toList();

        // Return an Optional of the list of BillResponse
        return Optional.ofNullable(billResponses.isEmpty() ? null : billResponses);
    }

    @Override
    public Optional<List<BillResponse>> fetchBillsByInstitution(SingleStringRequest singleStringRequest) {
        return Optional.of(
                BillUtil.billGlobalList.stream()
                        .filter(b-> b.getInstitutionCode().equalsIgnoreCase(singleStringRequest.getVal()))
                        .map(billUtil::mapBill_ToBillResponse)
                        .toList());
    }

    @Override
    public Optional<BillResponse> fetchBillByInstitutionAndName(BillFetchRequest billFetchRequest) {
        return Optional.of(
                BillUtil.billGlobalList.stream()
                        .filter(b->b.getInstitutionCode().equalsIgnoreCase(billFetchRequest.getInstitutionCode()) && b.getBill_Name().equalsIgnoreCase(billFetchRequest.getName()))
                        .map(billUtil::mapBill_ToBillResponse).findFirst().get());
    }
}

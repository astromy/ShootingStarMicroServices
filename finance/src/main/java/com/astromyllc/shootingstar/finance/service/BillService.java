package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
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
    public BillResponse createBill(BillRequest billRequest) {
        Optional<Bill> bill=billUtil.billGlobalList.stream().filter(b->b.getInstitutionCode().equals(billRequest.getInstitutionCode())).findFirst();
        if(bill.isEmpty()){
            Bill bill1=billUtil.mapBillRequest_ToBill(billRequest);
            billRepository.save(bill1);
            billUtil.billGlobalList.add(bill1);
            return billUtil.mapBill_ToBillResponse(bill1);
        }else{
            billRepository.save(billUtil.mapBillRequest_ToBill(billRequest,bill.get()));
        }
        return null;
    }

    @Override
    public List<BillResponse> createBills(List<BillRequest> billRequest) {
      /*  Optional<List<BillRequest>> Bills = billUtil.billGlobalList.stream()
                .map(br->billRequest.stream()
                        .filter(b->!b.getInstitutionCode().equals(br.getInstitutionCode())).).collect(Collectors.toList());
        */

        List<Bill> existingBills= (List<Bill>) billUtil.billGlobalList.stream()
                .map(br->billRequest.stream()
                        .filter(b->b.getInstitutionCode().equals(br.getInstitutionCode())).collect(Collectors.toList())
                        .stream().map(bm->billUtil.mapBillRequest_ToBill(bm,br)).collect(Collectors.toList())); //.orElse(existingBills.add(br))
     if(existingBills.size()>0) {
         billRepository.saveAll(existingBills);
         billUtil.billGlobalList.addAll(existingBills);
         return existingBills.stream().map(eb-> billUtil.mapBill_ToBillResponse(eb)).collect(Collectors.toList());
     }

        return null;
    }

    @Override
    public List<BillResponse> fetchBillsByInstitution(BillFetchRequest billFetchRequest) {
        return billUtil.billGlobalList.stream().filter(b->b.getInstitutionCode().equals(billFetchRequest.getInstitutionCode())).map(br->billUtil.mapBill_ToBillResponse(br)).collect(Collectors.toList());
    }

    @Override
    public BillResponse fetchBillByInstitutionAndName(BillFetchRequest billFetchRequest) {
        return billUtil.billGlobalList.stream().filter(b->b.getInstitutionCode().equals(billFetchRequest.getInstitutionCode()) && b.getBill_Name().equals(billFetchRequest.getName())).map(br->billUtil.mapBill_ToBillResponse(br)).findFirst().get();
    }
}

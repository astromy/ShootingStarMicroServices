package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillResponse;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.model.Bill;
import com.astromyllc.shootingstar.finance.repositoy.BillRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillUtil {
    private final BillRepository billRepository;
    public static List<Bill> billGlobalList = null;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    private void AllBills() {
        billGlobalList = billRepository.findAll();
        log.info("Global Bill List populated with {} records", billGlobalList.size());
    }


    public Bill mapBillRequest_ToBill(BillRequest billRequest) {
        return Bill.builder()
                .bill_Amount(billRequest.getBill_Amount())
                .bill_Cat(billRequest.getBill_Cat())
                .bill_Description(billRequest.getBill_Description())
                .bill_Name(billRequest.getBill_Name())
                .institutionCode(billRequest.getInstitutionCode())
                .creation_Date(LocalDateTime.now())
                .build();
    }

    public BillResponse mapBill_ToBillResponse(Bill bill1) {
        return BillResponse.builder()
                .bill_Amount(bill1.getBill_Amount())
                .bill_Cat(bill1.getBill_Cat())
                .bill_Description(bill1.getBill_Description())
                .bill_Name(bill1.getBill_Name())
                .institutionCode(bill1.getInstitutionCode())
                .creation_Date(bill1.getCreation_Date())
                .build();
    }

    public Bill mapBillRequest_ToBill(BillRequest billRequest, Bill bill) {
        bill.setBill_Amount(billRequest.getBill_Amount());
        bill.setBill_Cat(billRequest.getBill_Cat());
        bill.setBill_Description(billRequest.getBill_Description());
        bill.setBill_Name(billRequest.getBill_Name());
        bill.setInstitutionCode(billRequest.getInstitutionCode());
        return bill;
    }
}

package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.BillServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/finance")
public class BillController {

    private final BillServiceInterface billServiceInterface;

    @PostMapping("/create-bill")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<BillResponse> createBill(@RequestBody BillRequest billRequest) {
        log.info("Application  Received");
        return billServiceInterface.createBill(billRequest);
    }

    @PostMapping("/create-bills")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<BillResponse>> createBills(@RequestBody List<BillRequest> billRequest) {
        log.info("Application  Received");
        return billServiceInterface.createBills(billRequest);
    }


    @PostMapping("/get-bills-by-institution")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<BillResponse>> getBillsByInstitution(@RequestBody SingleStringRequest singleStringRequest) {
        log.info("Application  Received");
        return billServiceInterface.fetchBillsByInstitution(singleStringRequest);
    }


    @PostMapping("/get-bill-by-institution")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<BillResponse> getBillByInstitution(@RequestBody BillFetchRequest billFetchRequest) {
        log.info("Application  Received");
        return billServiceInterface.fetchBillByInstitutionAndName(billFetchRequest);
    }

}

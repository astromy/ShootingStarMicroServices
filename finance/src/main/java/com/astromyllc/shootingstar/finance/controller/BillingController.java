package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.BillingFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.BillingsServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final BillingsServiceInterface billingsServiceInterface;

    @PostMapping
    @RequestMapping("/api/finance/create-billing")
    @ResponseStatus(HttpStatus.CREATED)
    public BillingsResponse createBilling(@RequestBody BillingsRequest billingsRequest) {
        log.info("Application  Received");
        return billingsServiceInterface.updateBilling(billingsRequest);
    }

    @PostMapping
    @RequestMapping("/api/finance/create-billings")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<BillingsResponse>> createBillings(@RequestBody BillingsRequest billingsRequest) {
        log.info("Application  Received");
        return billingsServiceInterface.createBillings(billingsRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/get-billings-by-institution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getBillingsByInstitution(@RequestBody BillingFetchRequest billFetchRequest) {
        log.info("Application  Received");
        return billingsServiceInterface.fetchBillingsByInstitution(billFetchRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/get-billing-by-institution-and-student")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getBillingByInstitutionAndStudent(@RequestBody BillingFetchRequest billFetchRequest) {
        return billingsServiceInterface.fetchBillingByInstitutionAndStudent(billFetchRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/getStudentBilling")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getStudentBilling(@RequestBody BillingFetchRequest billFetchRequest) {
        return billingsServiceInterface.fetchBillingByInstitutionStudentClassTerm(billFetchRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/getClassBilling")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getClassBilling(@RequestBody BillingFetchRequest billFetchRequest) {
        return billingsServiceInterface.fetchClassBillingByInstitution(billFetchRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/getSchoolBilling")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getSchoolBilling(@RequestBody BillingFetchRequest billFetchRequest) {
        return billingsServiceInterface.fetchSchoolBillingByInstitution(billFetchRequest);
    }

}

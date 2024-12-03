package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.request.Bill_PaymentRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillResponse;
import com.astromyllc.shootingstar.finance.dto.response.Bill_PaymentResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.BillServiceInterface;
import com.astromyllc.shootingstar.finance.serviceInterface.Bill_PaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BillPaymentController {

    private final Bill_PaymentServiceInterface billPaymentServiceInterface;

    @PostMapping
    @RequestMapping("/api/finance/create-billPayment")
    @ResponseStatus(HttpStatus.CREATED)
    public Bill_PaymentResponse createBillPayment(@RequestBody Bill_PaymentRequest billPaymentRequest) {
        return billPaymentServiceInterface.createBillPayment(billPaymentRequest);
    }

    @PostMapping
    @RequestMapping("/api/finance/create-billPayments")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Bill_PaymentResponse> createBillPayments(@RequestBody List<Bill_PaymentRequest> billPaymentRequests) {
        return billPaymentServiceInterface.createBillPayments(billPaymentRequests);
    }


    @PostMapping
    @RequestMapping("/api/finance/get-billPayments-by-institution")
    @ResponseStatus(HttpStatus.OK)
    public List<Bill_PaymentResponse> getBillPaymentsByInstitution(@RequestBody BillFetchRequest billFetchRequest) {
        log.info("Application  Received");
        return billPaymentServiceInterface.fetchBillPaymentsByInstitution(billFetchRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/get-billPayment-by-institutionAndName")
    @ResponseStatus(HttpStatus.OK)
    public Bill_PaymentResponse getBillPaymentByInstitution(@RequestBody BillFetchRequest billFetchRequest) {
        log.info("Application  Received");
        return billPaymentServiceInterface.fetchBillPaymentsByInstitutionAndName(billFetchRequest);
    }

}

package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Bill_PaymentRequest;
import com.astromyllc.shootingstar.finance.dto.response.Bill_PaymentResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.Bill_PaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Slf4j
public class BillPaymentController {

    private final Bill_PaymentServiceInterface billPaymentServiceInterface;

    /**
     * Record a single payment (in-person or online).
     * This atomically:
     * 1. Saves the Bill_Payment record.
     * 2. Updates Student_Bill (amountPaid ++, amountBalance recalculated).
     */
    @PostMapping("/create-billPayment")
    @ResponseStatus(HttpStatus.CREATED)
    public Bill_PaymentResponse createBillPayment(@RequestBody Bill_PaymentRequest request) {
        log.info("Payment received for student {} amount {}", request.getStudentId(), request.getPaymentAmount());
        return billPaymentServiceInterface.createBillPayment(request);
    }

    /**
     * Batch payment entry — e.g. end-of-day cash receipts
     */
    @PostMapping("/create-billPayments")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Bill_PaymentResponse> createBillPayments(@RequestBody List<Bill_PaymentRequest> requests) {
        log.info("Batch payment received: {} records", requests.size());
        return billPaymentServiceInterface.createBillPayments(requests);
    }

    /**
     * All payments for an institution (payment history report)
     */
    @PostMapping("/get-billPayments-by-institution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<Bill_PaymentResponse>> getByInstitution(@RequestBody BillFetchRequest request) {
        return billPaymentServiceInterface.fetchBillPaymentsByInstitution(request);
    }

    /**
     * All payments for a single student (student payment history)
     */
    @PostMapping("/get-billPayments-by-student")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<Bill_PaymentResponse>> getByStudent(@RequestBody BillFetchRequest request) {
        return billPaymentServiceInterface.fetchPaymentsByStudent(
                request.getName(),           // reuse 'name' field as studentId
                request.getInstitutionCode()
        );
    }

    /**
     * Payments for a student scoped to a specific term and academic year
     */
    @PostMapping("/get-billPayments-by-student-term")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<Bill_PaymentResponse>> getByStudentTermYear(
            @RequestBody Bill_PaymentRequest request) {
        return billPaymentServiceInterface.fetchPaymentsByStudentTermYear(
                request.getStudentId(),
                request.getInstitutionCode(),
                request.getTerm(),
                request.getAcademicYear()
        );
    }

    @PostMapping("/get-billPayment-by-institutionAndName")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Bill_PaymentResponse> getByInstitutionAndName(@RequestBody BillFetchRequest request) {
        return billPaymentServiceInterface.fetchBillPaymentsByInstitutionAndName(request);
    }
}
package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.request.Bill_PaymentRequest;
import com.astromyllc.shootingstar.finance.dto.response.Bill_PaymentResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.Bill_PaymentServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class Bill_PaymentService implements Bill_PaymentServiceInterface {
    @Override
    public Bill_PaymentResponse createBillPayment(Bill_PaymentRequest billPaymentRequest) {
        return null;
    }

    @Override
    public List<Bill_PaymentResponse> createBillPayments(List<Bill_PaymentRequest> billPaymentRequests) {
        return null;
    }

    @Override
    public List<Bill_PaymentResponse> fetchBillPaymentsByInstitution(BillFetchRequest billFetchRequest) {
        return null;
    }

    @Override
    public Bill_PaymentResponse fetchBillPaymentsByInstitutionAndName(BillFetchRequest billFetchRequest) {
        return null;
    }
}

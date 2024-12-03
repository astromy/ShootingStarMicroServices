package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.request.Bill_PaymentRequest;
import com.astromyllc.shootingstar.finance.dto.response.Bill_PaymentResponse;

import java.util.List;

public interface Bill_PaymentServiceInterface {
    public Bill_PaymentResponse createBillPayment(Bill_PaymentRequest billPaymentRequest);

    public List<Bill_PaymentResponse> createBillPayments(List<Bill_PaymentRequest> billPaymentRequests);

    public List<Bill_PaymentResponse> fetchBillPaymentsByInstitution(BillFetchRequest billFetchRequest);

    public Bill_PaymentResponse fetchBillPaymentsByInstitutionAndName(BillFetchRequest billFetchRequest);
}

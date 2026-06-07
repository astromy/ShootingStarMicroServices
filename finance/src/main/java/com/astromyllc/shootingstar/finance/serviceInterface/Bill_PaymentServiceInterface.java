package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Bill_PaymentRequest;
import com.astromyllc.shootingstar.finance.dto.response.Bill_PaymentResponse;

import java.util.List;
import java.util.Optional;

public interface Bill_PaymentServiceInterface {

    Bill_PaymentResponse createBillPayment(Bill_PaymentRequest billPaymentRequest);

    List<Bill_PaymentResponse> createBillPayments(List<Bill_PaymentRequest> billPaymentRequests);

    Optional<List<Bill_PaymentResponse>> fetchBillPaymentsByInstitution(BillFetchRequest billFetchRequest);

    Optional<List<Bill_PaymentResponse>> fetchPaymentsByStudent(String studentId, String institutionCode);

    Optional<List<Bill_PaymentResponse>> fetchPaymentsByStudentTermYear(
            String studentId, String institutionCode, String term, String academicYear);

    Optional<Bill_PaymentResponse> fetchBillPaymentsByInstitutionAndName(BillFetchRequest billFetchRequest);
}
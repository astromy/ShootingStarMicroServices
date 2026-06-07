package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.dto.request.Bill_PaymentRequest;
import com.astromyllc.shootingstar.finance.dto.response.Bill_PaymentResponse;
import com.astromyllc.shootingstar.finance.model.Bill_Payment;
import com.astromyllc.shootingstar.finance.repositoy.Bill_PaymentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Bill_PaymentUtil {

    public static List<Bill_Payment> billPaymentGlobalList = new ArrayList<>();
    private final Bill_PaymentRepository billPaymentRepository;

    @PostConstruct
    private void loadAll() {
        billPaymentGlobalList = billPaymentRepository.findAll();
        log.info("Global Bill_Payment list populated with {} records", billPaymentGlobalList.size());
    }

    public Bill_Payment mapRequest_ToPayment(Bill_PaymentRequest r) {
        return Bill_Payment.builder()
                .paymentAmount(r.getPaymentAmount())
                .paymentDate(LocalDateTime.now())
                .studentId(r.getStudentId())
                .paidBy(r.getPaidBy())
                .recieptNum(r.getRecieptNum())
                .term(r.getTerm())
                .academicYear(r.getAcademicYear())
                .institutionCode(r.getInstitutionCode())
                .paymentMethod(r.getPaymentMethod() != null ? r.getPaymentMethod() : "CASH")
                .externalReference(r.getExternalReference())
                .build();
    }

    public Bill_PaymentResponse mapPayment_ToResponse(Bill_Payment p) {
        return Bill_PaymentResponse.builder()
                .billPaymentId(p.getBillPaymentId())
                .paymentAmount(p.getPaymentAmount())
                .paymentDate(p.getPaymentDate())
                .studentId(p.getStudentId())
                .paidBy(p.getPaidBy())
                .recieptNum(p.getRecieptNum())
                .term(p.getTerm())
                .academicYear(p.getAcademicYear())
                .institutionCode(p.getInstitutionCode())
                .paymentMethod(p.getPaymentMethod())
                .externalReference(p.getExternalReference())
                .build();
    }
}
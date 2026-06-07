package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Bill_PaymentRequest;
import com.astromyllc.shootingstar.finance.dto.response.Bill_PaymentResponse;
import com.astromyllc.shootingstar.finance.model.Bill_Payment;
import com.astromyllc.shootingstar.finance.model.Student_Bill;
import com.astromyllc.shootingstar.finance.repositoy.Bill_PaymentRepository;
import com.astromyllc.shootingstar.finance.repositoy.Student_BillRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.Bill_PaymentServiceInterface;
import com.astromyllc.shootingstar.finance.utils.Bill_PaymentUtil;
import com.astromyllc.shootingstar.finance.utils.Student_BillUtil;
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
public class Bill_PaymentService implements Bill_PaymentServiceInterface {

    private final Bill_PaymentRepository billPaymentRepository;
    private final Student_BillRepository studentBillRepository;
    private final Bill_PaymentUtil billPaymentUtil;
    private final Student_BillUtil studentBillUtil;

    /**
     * Core payment flow:
     * 1. Persist the Bill_Payment record.
     * 2. Find the student's Student_Bill.
     * 3. Apply the payment (amountPaid += payment, amountBalance recalculated).
     * 4. Save updated Student_Bill.
     * 5. Update both global lists.
     * 6. Return the payment response.
     */
    @Override
    public Bill_PaymentResponse createBillPayment(Bill_PaymentRequest r) {
        log.info("Processing payment of {} for student {} at institution {}",
                r.getPaymentAmount(), r.getStudentId(), r.getInstitutionCode());

        // 1. Build and persist payment record
        Bill_Payment payment = billPaymentUtil.mapRequest_ToPayment(r);
        billPaymentRepository.save(payment);
        Bill_PaymentUtil.billPaymentGlobalList.add(payment);

        // 2. Find student bill — required for balance update
        Optional<Student_Bill> studentBillOpt = Student_BillUtil.studentBillsGlobalList.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(r.getStudentId())
                        && s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                .findFirst();

        if (studentBillOpt.isPresent()) {
            // 3 & 4. Apply payment, save
            Student_Bill updated = studentBillUtil.applyPayment(studentBillOpt.get(), r.getPaymentAmount());
            studentBillRepository.save(updated);

            // 5. Sync global list
            int idx = Student_BillUtil.studentBillsGlobalList.indexOf(studentBillOpt.get());
            if (idx >= 0) Student_BillUtil.studentBillsGlobalList.set(idx, updated);

            log.info("Student bill updated for {}: paid={}, balance={}",
                    r.getStudentId(), updated.getAmountPaid(), updated.getAmountBalance());
        } else {
            log.warn("No Student_Bill found for student {} at institution {} — payment recorded but balance not updated",
                    r.getStudentId(), r.getInstitutionCode());
        }

        return billPaymentUtil.mapPayment_ToResponse(payment);
    }

    @Override
    public List<Bill_PaymentResponse> createBillPayments(List<Bill_PaymentRequest> requests) {
        return requests.stream()
                .map(this::createBillPayment)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<Bill_PaymentResponse>> fetchBillPaymentsByInstitution(BillFetchRequest r) {
        return Optional.of(
                Bill_PaymentUtil.billPaymentGlobalList.stream()
                        .filter(p -> p.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                        .map(billPaymentUtil::mapPayment_ToResponse)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<List<Bill_PaymentResponse>> fetchPaymentsByStudent(String studentId, String institutionCode) {
        return Optional.of(
                Bill_PaymentUtil.billPaymentGlobalList.stream()
                        .filter(p -> p.getStudentId().equalsIgnoreCase(studentId)
                                && p.getInstitutionCode().equalsIgnoreCase(institutionCode))
                        .map(billPaymentUtil::mapPayment_ToResponse)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<List<Bill_PaymentResponse>> fetchPaymentsByStudentTermYear(
            String studentId, String institutionCode, String term, String academicYear) {
        return Optional.of(
                Bill_PaymentUtil.billPaymentGlobalList.stream()
                        .filter(p -> p.getStudentId().equalsIgnoreCase(studentId)
                                && p.getInstitutionCode().equalsIgnoreCase(institutionCode)
                                && p.getTerm().equalsIgnoreCase(term)
                                && p.getAcademicYear().equalsIgnoreCase(academicYear))
                        .map(billPaymentUtil::mapPayment_ToResponse)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Bill_PaymentResponse> fetchBillPaymentsByInstitutionAndName(BillFetchRequest r) {
        return Bill_PaymentUtil.billPaymentGlobalList.stream()
                .filter(p -> p.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                .map(billPaymentUtil::mapPayment_ToResponse)
                .findFirst();
    }
}
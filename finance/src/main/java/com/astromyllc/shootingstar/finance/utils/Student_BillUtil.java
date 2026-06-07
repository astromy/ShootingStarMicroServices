package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;
import com.astromyllc.shootingstar.finance.model.Student_Bill;
import com.astromyllc.shootingstar.finance.repositoy.Student_BillRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Student_BillUtil {

    public static List<Student_Bill> studentBillsGlobalList = new ArrayList<>();
    private final Student_BillRepository studentBillRepository;

    @PostConstruct
    private void loadAll() {
        studentBillsGlobalList = studentBillRepository.findAll();
        log.info("Global StudentBill List populated with {} records", studentBillsGlobalList.size());
    }

    /**
     * Create a brand-new Student_Bill row (first ever bill for this student).
     */
    public Student_Bill mapStudentBillRequest_ToStudentBill(Student_BillRequest r) {
        double amountDue = r.getAmountDue() != null ? r.getAmountDue() : 0.0;
        double amountPaid = r.getAmountPaid() != null ? r.getAmountPaid() : 0.0;
        double amountBalance = amountDue - amountPaid;

        return Student_Bill.builder()
                .studentId(r.getStudentId())
                .institutionCode(r.getInstitutionCode())
                .studentClass(r.getStudentClass())
                .term(r.getTerm())
                .academicYear(r.getAcademicYear())
                .amountDue(amountDue)
                .amountPaid(amountPaid)
                .amountBalance(amountBalance)
                .oldBalance(0.0)   // first bill — no prior balance
                .build();
    }

    /**
     * Apply a new billing batch to an existing Student_Bill.
     * <p>
     * Rules (per product owner):
     * oldBalance   = amountDue BEFORE this batch (snapshot)
     * amountDue    += new billing batch total
     * amountBalance = amountDue - amountPaid  (recalculated)
     * amountPaid   stays unchanged — payments are recorded separately
     */
    public Student_Bill mapStudentBillRequest_ToStudentBill(Student_BillRequest r, Student_Bill existing) {
        double currentDue = existing.getAmountDue() != null ? existing.getAmountDue() : 0.0;
        double currentPaid = existing.getAmountPaid() != null ? existing.getAmountPaid() : 0.0;
        double addedBatch = r.getAmountDue() != null ? r.getAmountDue() : 0.0;

        double newDue = currentDue + addedBatch;
        double newBalance = newDue - currentPaid;

        existing.setOldBalance(currentDue);          // snapshot before this batch
        existing.setAmountDue(newDue);
        existing.setAmountBalance(newBalance);
        existing.setStudentClass(r.getStudentClass());
        existing.setTerm(r.getTerm());
        if (r.getAcademicYear() != null) existing.setAcademicYear(r.getAcademicYear());
        return existing;
    }

    /**
     * Apply a payment to an existing Student_Bill.
     * amountPaid += payment, amountBalance recalculated.
     * Called by Bill_PaymentService after persisting the Bill_Payment record.
     */
    public Student_Bill applyPayment(Student_Bill existing, double paymentAmount) {
        double currentPaid = existing.getAmountPaid() != null ? existing.getAmountPaid() : 0.0;
        double currentDue = existing.getAmountDue() != null ? existing.getAmountDue() : 0.0;

        double newPaid = currentPaid + paymentAmount;
        double newBalance = currentDue - newPaid;

        existing.setAmountPaid(newPaid);
        existing.setAmountBalance(newBalance);
        return existing;
    }

    public Student_BillResponse mapStudentBill_ToStudentBillResponse(Student_Bill s) {
        return Student_BillResponse.builder()
                .studentBillId(s.getStudentBillId())
                .studentId(s.getStudentId())
                .institutionCode(s.getInstitutionCode())
                .studentClass(s.getStudentClass())
                .term(s.getTerm())
                .amountDue(s.getAmountDue())
                .amountPaid(s.getAmountPaid())
                .amountBalance(s.getAmountBalance())
                .oldBalance(s.getOldBalance())
                .build();
    }
}
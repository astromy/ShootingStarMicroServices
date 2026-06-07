package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.repositoy.BillingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingsUtil {

    public static List<Billings> billingGlobalList = new ArrayList<>();
    private final BillingsRepository billingsRepository;
    @Value("${gateway.host}")
    private String host;

    @PostConstruct
    private void loadAll() {
        billingGlobalList = billingsRepository.findAll();
        log.info("Global Billing List populated with {} records", billingGlobalList.size());
    }

    public Billings mapBillingRequest_ToBilling(
            String inst, String studClass, String term, String academicYear,
            Double amnt, Double billBal, String billDesc, String billName, String student) {
        return Billings.builder()
                .billingDate(LocalDateTime.now())
                .institutionCode(inst)
                .billamnt(amnt)
                .billamntbal(billBal)
                .BillDisc(billDesc)
                .term(term)
                .academicYear(academicYear)
                .studentClass(studClass)
                .billname(billName)
                .studentId(student)
                .build();
    }

    public BillingsResponse mapBillings_ToBillingResponse(Billings b) {
        return BillingsResponse.builder()
                .billingId(b.getBillingId())
                .billingDate(b.getBillingDate())
                .institutionCode(b.getInstitutionCode())
                .billamnt(b.getBillamnt())
                .billamntbal(b.getBillamntbal())
                .BillDisc(b.getBillDisc())
                .term(b.getTerm())
                .studentClass(b.getStudentClass())
                .billname(b.getBillname())
                .studentId(b.getStudentId())
                .build();
    }

    /**
     * Build a Student_BillRequest from a summarised Billings row.
     * amountPaid is intentionally 0.0 here — the Student_BillService
     * will read the existing record and carry forward the actual paid amount.
     */
    public Student_BillRequest mapBilling_ToStudentBill(Billings sb) {
        return Student_BillRequest.builder()
                .amountDue(sb.getBillamnt())
                .amountPaid(0.0)
                .studentId(sb.getStudentId())
                .term(sb.getTerm())
                .academicYear(sb.getAcademicYear())
                .studentClass(sb.getStudentClass())
                .institutionCode(sb.getInstitutionCode())
                .amountBalance(sb.getBillamntbal())
                .build();
    }
}
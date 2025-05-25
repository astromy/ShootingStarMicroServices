package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.model.Student_Bill;
import com.astromyllc.shootingstar.finance.repositoy.BillingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingsUtil {
    private final BillingsRepository billingsRepository;
    public static List<Billings> billingGlobalList;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${gateway.host}")
    private String host;

    @PostConstruct
    private void fetAllBillings() {
        billingGlobalList = billingsRepository.findAll();
        log.info("Global Billing List populated with {} records", billingGlobalList.size());
    }


    public Billings mapBillingRequest_ToBilling(String inst, String studClass, String term, Double amnt,Double billBal, String billDesc, String billName, String student) {
        return Billings.builder()
                //.billingDate(LocalDateTime.parse(billingsRequest.getBillingDate(), formatter))
                .billingDate(LocalDateTime.now())
                .institutionCode(inst)
                .billamnt(amnt)
                .billamntbal(billBal)
                .BillDisc(billDesc)
                .term(term)
                .studentClass(studClass)
                .billname(billName)
                .studentId(student)
                .build();
    }

    public Billings mapBillingRequest_ToBilling(BillingsRequest billingsRequest, Billings billings) {

        //billings.setBillamntbal(billingsRequest.getBillamntbal());
        return billings;
    }

    public BillingsResponse mapBillings_ToBillingResponse(Billings billings1) {
       return  BillingsResponse.builder()
                .billingDate(billings1.getBillingDate())
                .institutionCode(billings1.getInstitutionCode())
                .billamnt(billings1.getBillamnt())
                .billamntbal(billings1.getBillamntbal())
                .BillDisc(billings1.getBillDisc())
                .term(billings1.getTerm())
                .studentClass(billings1.getStudentClass())
                .billname(billings1.getBillname())
                .studentId(billings1.getStudentId())
                .build();
    }


    public Student_BillRequest mapBilling_ToStudentBill(Billings sb) {
        return Student_BillRequest.builder()
                .studentBillId(sb.getBillingId())
                .amountDue(sb.getBillamnt())
                .amountPaid(0.0)
                .studentId(sb.getStudentId())
                .term(sb.getTerm())
                .studentClass(sb.getStudentClass())
                .institutionCode(sb.getInstitutionCode())
                .amountBalance(sb.getBillamntbal())
                .build();
    }
}

package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.repositoy.BillingsRepository;
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

    @Bean
    private void fetAllBillings() {
        billingGlobalList = billingsRepository.findAll();
        log.info("Global Billing List populated with {} records", billingGlobalList.size());
    }


    public Billings mapBillingRequest_ToBilling(BillingsRequest billingsRequest) {
        return Billings.builder()
                .billingDate(LocalDateTime.parse(billingsRequest.getBillingDate(), formatter))
                .institutionCode(billingsRequest.getInstitutionCode())
                .billamnt(billingsRequest.getBillamnt())
                .billamntbal(billingsRequest.getBillamntbal())
                .BillDisc(billingsRequest.getBillDisc())
                .term(billingsRequest.getTerm())
                .studentClass(billingsRequest.getStudentClass())
                .billname(billingsRequest.getBillname())
                .studentId(billingsRequest.getStudentId())
                .build();
    }

    public Billings mapBillingRequest_ToBilling(BillingsRequest billingsRequest, Billings billings) {
        billings.setBillingDate(LocalDateTime.parse(billingsRequest.getBillingDate(), formatter));
        billings.setInstitutionCode(billingsRequest.getInstitutionCode());
        billings.setBillamnt(billingsRequest.getBillamnt());
        billings.setBillamntbal(billingsRequest.getBillamntbal());
        billings.setBillDisc(billingsRequest.getBillDisc());
        billings.setTerm(billingsRequest.getTerm());
        billings.setStudentClass(billingsRequest.getStudentClass());
        billings.setBillname(billingsRequest.getBillname());
        billings.setStudentId(billingsRequest.getStudentId());
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
}

package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;
import com.astromyllc.shootingstar.finance.model.Student_Bill;
import com.astromyllc.shootingstar.finance.repositoy.Student_BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class Student_BillUtil {
    private final Student_BillRepository studentBillRepository;
    public static List<Student_Bill> studentBillsGlobalList;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Bean
    private void fetAllStudentBill() {
        studentBillsGlobalList = studentBillRepository.findAll();
        log.info("Global StudentBill List populated with {} records", studentBillsGlobalList.size());
    }

    public Student_Bill mapStudentBillRequest_ToStudentBill(Student_BillRequest studentBillRequest) {
        return Student_Bill.builder()
                .studentClass(studentBillRequest.getStudentClass())
                .studentId(studentBillRequest.getStudentId())
                .amountBalance(studentBillRequest.getAmountBalance())
                .amountDue(studentBillRequest.getAmountDue())
                .term(studentBillRequest.getTerm())
                .amountPaid(studentBillRequest.getAmountPaid())
                .institutionCode(studentBillRequest.getInstitutionCode())
                .oldBalance(studentBillRequest.getOldBalance())
                .build();
    }

    public Student_Bill mapStudentBillRequest_ToStudentBill(Student_BillRequest studentBillRequest, Student_Bill studentBill) {
        studentBill.setStudentClass(studentBillRequest.getStudentClass());
        studentBill.setStudentId(studentBillRequest.getStudentId());
        studentBill.setInstitutionCode(studentBillRequest.getInstitutionCode());
        studentBill.setTerm(studentBillRequest.getTerm());
        studentBill.setAmountDue(studentBillRequest.getAmountDue());
        studentBill.setAmountBalance(studentBillRequest.getAmountBalance());
        studentBill.setAmountPaid(studentBillRequest.getAmountPaid());
        studentBill.setOldBalance(studentBillRequest.getOldBalance());
        return  studentBill;
    }

    public Student_BillResponse mapStudentBill_ToStudentBillResponse(Student_Bill studentBill1) {
        return Student_BillResponse.builder()
                .studentBillId(studentBill1.getStudentBillId())
                .studentClass(studentBill1.getStudentClass())
                .studentId(studentBill1.getStudentId())
                .amountBalance(studentBill1.getAmountBalance())
                .amountDue(studentBill1.getAmountDue())
                .term(studentBill1.getTerm())
                .amountPaid(studentBill1.getAmountPaid())
                .institutionCode(studentBill1.getInstitutionCode())
                .oldBalance(studentBill1.getOldBalance())
                .build();
    }
}

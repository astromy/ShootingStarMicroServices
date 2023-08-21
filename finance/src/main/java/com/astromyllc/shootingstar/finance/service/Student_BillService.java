package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.StudentBillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;
import com.astromyllc.shootingstar.finance.model.Student_Bill;
import com.astromyllc.shootingstar.finance.repositoy.Student_BillRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.Student_BillServiceInterface;
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
public class Student_BillService implements Student_BillServiceInterface {
    private final Student_BillRepository studentBillRepository;
    private final Student_BillUtil studentBillUtil;

    @Override
    public List<Student_BillResponse> createStudentsBill(List<Student_BillRequest> studentBillRequest) {
        // Optional<Student_Bill> studentBillUtil.studentBillsGlobalList.stream().filter()
        return null;
    }

    @Override
    public Student_BillResponse createStudentBill(Student_BillRequest studentBillRequest) {
        Optional<Student_Bill> studentBill = studentBillUtil.studentBillsGlobalList.stream().filter(s -> s.getInstitutionCode().equals(studentBillRequest.getInstitutionCode()) && s.getStudentId().equals(studentBillRequest.getStudentId())).findFirst();
        if (studentBill.isEmpty()) {
            Student_Bill studentBill1 = studentBillUtil.mapStudentBillRequest_ToStudentBill(studentBillRequest);
            studentBillRepository.save(studentBill1);
            studentBillUtil.studentBillsGlobalList.add(studentBill1);
            return studentBillUtil.mapStudentBill_ToStudentBillResponse(studentBill1);
        } else {
            studentBillRepository.save(studentBillUtil.mapStudentBillRequest_ToStudentBill(studentBillRequest, studentBill.get()));
        }
        return null;
    }

    @Override
    public List<Student_BillResponse> fetchStudentBillsByInstitution(StudentBillFetchRequest studentBillFetchRequest) {
        return studentBillUtil.studentBillsGlobalList.stream().filter(
                        s -> s.getInstitutionCode().equals(studentBillFetchRequest.getInstitutionCode()))
                .map(sr -> studentBillUtil.mapStudentBill_ToStudentBillResponse(sr)).collect(Collectors.toList());
    }

    @Override
    public Student_BillResponse fetchStudentBillByIdAndInstitution(StudentBillFetchRequest studentBillFetchRequest) {
        return studentBillUtil.studentBillsGlobalList.stream().filter(
                        s -> s.getInstitutionCode().equals(studentBillFetchRequest.getInstitutionCode())
                                && s.getStudentId().equals(studentBillFetchRequest.getStudentId()))
                .map(sr -> studentBillUtil.mapStudentBill_ToStudentBillResponse(sr)).findFirst().get();
    }

    @Override
    public List<Student_BillResponse> fetchOwingStudentsByInstitution(StudentBillFetchRequest studentBillFetchRequest) {
        return null;
    }
}

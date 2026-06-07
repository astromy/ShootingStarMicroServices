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
    public List<Student_BillResponse> createStudentsBill(List<Student_BillRequest> requests) {
        return requests.stream()
                .map(r -> {
                    Optional<Student_Bill> existing = Student_BillUtil.studentBillsGlobalList.stream()
                            .filter(s -> s.getStudentId().equalsIgnoreCase(r.getStudentId())
                                    && s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                            .findFirst();

                    return existing.map(bill -> {
                        // Update: snapshot oldBalance, accumulate amountDue, recalculate balance
                        Student_Bill updated = studentBillUtil.mapStudentBillRequest_ToStudentBill(r, bill);
                        studentBillRepository.save(updated);
                        int idx = Student_BillUtil.studentBillsGlobalList.indexOf(bill);
                        if (idx >= 0) Student_BillUtil.studentBillsGlobalList.set(idx, updated);
                        return studentBillUtil.mapStudentBill_ToStudentBillResponse(updated);
                    }).orElseGet(() -> {
                        // Create: brand-new account
                        Student_Bill newBill = studentBillUtil.mapStudentBillRequest_ToStudentBill(r);
                        studentBillRepository.save(newBill);
                        Student_BillUtil.studentBillsGlobalList.add(newBill);
                        return studentBillUtil.mapStudentBill_ToStudentBillResponse(newBill);
                    });
                })
                .collect(Collectors.toList());
    }

    @Override
    public Student_BillResponse createStudentBill(Student_BillRequest r) {
        Optional<Student_Bill> existing = Student_BillUtil.studentBillsGlobalList.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(r.getStudentId())
                        && s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                .findFirst();

        if (existing.isPresent()) {
            Student_Bill updated = studentBillUtil.mapStudentBillRequest_ToStudentBill(r, existing.get());
            studentBillRepository.save(updated);
            int idx = Student_BillUtil.studentBillsGlobalList.indexOf(existing.get());
            if (idx >= 0) Student_BillUtil.studentBillsGlobalList.set(idx, updated);
            return studentBillUtil.mapStudentBill_ToStudentBillResponse(updated);
        }

        Student_Bill newBill = studentBillUtil.mapStudentBillRequest_ToStudentBill(r);
        studentBillRepository.save(newBill);
        Student_BillUtil.studentBillsGlobalList.add(newBill);
        return studentBillUtil.mapStudentBill_ToStudentBillResponse(newBill);
    }

    @Override
    public Optional<List<Student_BillResponse>> fetchStudentBillsByInstitution(StudentBillFetchRequest r) {
        return Optional.of(Student_BillUtil.studentBillsGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                .map(studentBillUtil::mapStudentBill_ToStudentBillResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<Student_BillResponse>> fetchStudentBillsByInstitutionClass(StudentBillFetchRequest r) {
        return Optional.of(Student_BillUtil.studentBillsGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode())
                        && s.getStudentClass().equalsIgnoreCase(r.getStudentClass()))
                .map(studentBillUtil::mapStudentBill_ToStudentBillResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<Student_BillResponse> fetchStudentBillByIdAndInstitution(StudentBillFetchRequest r) {
        return Student_BillUtil.studentBillsGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode())
                        && s.getStudentId().equalsIgnoreCase(r.getStudentId()))
                .map(studentBillUtil::mapStudentBill_ToStudentBillResponse)
                .findFirst();
    }

    @Override
    public Optional<List<Student_BillResponse>> fetchOwingStudentsByInstitution(StudentBillFetchRequest r) {
        return Optional.of(Student_BillUtil.studentBillsGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode())
                        && s.getAmountBalance() != null
                        && s.getAmountBalance() > 0.0)
                .map(studentBillUtil::mapStudentBill_ToStudentBillResponse)
                .collect(Collectors.toList()));
    }
}
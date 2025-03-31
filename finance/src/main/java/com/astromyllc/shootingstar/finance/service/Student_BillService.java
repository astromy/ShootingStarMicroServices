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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class Student_BillService implements Student_BillServiceInterface {
    private final Student_BillRepository studentBillRepository;
    private final Student_BillUtil studentBillUtil;

    @Override
    public List<Student_BillResponse> createStudentsBill(List<Student_BillRequest> studentBillRequest) {
        return studentBillRequest.stream()
                .map(request -> {
                    // Stream through the global list and filter based on institutionCode and studentId from each request
                    Optional<Student_Bill> studentBill = Student_BillUtil.studentBillsGlobalList.stream()
                            .filter(s -> s.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())
                                    && s.getStudentId().equalsIgnoreCase(request.getStudentId()))
                            .findFirst();  // Find the first match or none (returns Optional)

                    return studentBill.map(existingBill -> {
                        // If a studentBill is found, update it
                        Student_Bill updatedBill = studentBillUtil.mapStudentBillRequest_ToStudentBill(request, existingBill);
                        studentBillRepository.save(updatedBill);

                        // Update the global list with the updated bill
                        int index = Student_BillUtil.studentBillsGlobalList.indexOf(existingBill);
                        if (index != -1) {
                            // Replace the old studentBill with the updated one in the global list
                            Student_BillUtil.studentBillsGlobalList.set(index, updatedBill);
                        }

                        // Return the Student_BillResponse for the updated bill
                        return studentBillUtil.mapStudentBill_ToStudentBillResponse(updatedBill);
                    }).orElseGet(() -> {
                        // If no studentBill is found, create a new one, save it, and add to the global list
                        Student_Bill newStudentBill = studentBillUtil.mapStudentBillRequest_ToStudentBill(request);
                        studentBillRepository.save(newStudentBill);
                        Student_BillUtil.studentBillsGlobalList.add(newStudentBill);

                        // Return the Student_BillResponse for the new bill
                        return studentBillUtil.mapStudentBill_ToStudentBillResponse(newStudentBill);
                    });
                })
                .toList();
    }

    @Override
    public Student_BillResponse createStudentBill(Student_BillRequest studentBillRequest) {
        Optional<Student_Bill> studentBill = Student_BillUtil.studentBillsGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(studentBillRequest.getInstitutionCode()) && s.getStudentId().equalsIgnoreCase(studentBillRequest.getStudentId())).findFirst();
        if (studentBill.isEmpty()) {
            Student_Bill studentBill1 = studentBillUtil.mapStudentBillRequest_ToStudentBill(studentBillRequest);
            studentBillRepository.save(studentBill1);
            Student_BillUtil.studentBillsGlobalList.add(studentBill1);
            return studentBillUtil.mapStudentBill_ToStudentBillResponse(studentBill1);
        } else {
            studentBillRepository.save(studentBillUtil.mapStudentBillRequest_ToStudentBill(studentBillRequest, studentBill.get()));
        }
        return null;
    }

    @Override
    public List<Student_BillResponse> fetchStudentBillsByInstitution(StudentBillFetchRequest studentBillFetchRequest) {
        return Student_BillUtil.studentBillsGlobalList.stream().filter(
                        s -> s.getInstitutionCode().equalsIgnoreCase(studentBillFetchRequest.getInstitutionCode()))
                .map(studentBillUtil::mapStudentBill_ToStudentBillResponse).toList();
    }

    @Override
    public List<Student_BillResponse> fetchStudentBillsByInstitutionClass(StudentBillFetchRequest studentBillFetchRequest) {
        return Student_BillUtil.studentBillsGlobalList.stream().filter(
                        s -> s.getInstitutionCode().equalsIgnoreCase(studentBillFetchRequest.getInstitutionCode())
                && s.getStudentClass().equalsIgnoreCase(studentBillFetchRequest.getStudentClass()))
                .map(studentBillUtil::mapStudentBill_ToStudentBillResponse).toList();
    }

    @Override
    public Student_BillResponse fetchStudentBillByIdAndInstitution(StudentBillFetchRequest studentBillFetchRequest) {
        return Student_BillUtil.studentBillsGlobalList.stream().filter(
                        s -> s.getInstitutionCode().equalsIgnoreCase(studentBillFetchRequest.getInstitutionCode())
                                && s.getStudentId().equalsIgnoreCase(studentBillFetchRequest.getStudentId()))
                .map(studentBillUtil::mapStudentBill_ToStudentBillResponse).findFirst().get();
    }

    @Override
    public List<Student_BillResponse> fetchOwingStudentsByInstitution(StudentBillFetchRequest studentBillFetchRequest) {
        return null;
    }
}

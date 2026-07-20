package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.DynamicStringRequest;
import com.astromyllc.shootingstar.finance.dto.request.StudentBillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;

import java.util.List;
import java.util.Optional;

public interface Student_BillServiceInterface {
    public List<Student_BillResponse> createStudentsBill(List<Student_BillRequest> studentBillRequest);

    public Student_BillResponse createStudentBill(Student_BillRequest studentBillRequest);

    public Optional<List<Student_BillResponse>> fetchStudentBillsByInstitution(StudentBillFetchRequest studentBillFetchRequest);

    public Optional<Student_BillResponse> fetchStudentBillByIdAndInstitution(StudentBillFetchRequest studentBillFetchRequest);

    public Optional<List<Student_BillResponse>> fetchOwingStudentsByInstitution(StudentBillFetchRequest studentBillFetchRequest);

    public Optional<List<Student_BillResponse>> fetchStudentBillsByInstitutionClass(DynamicStringRequest studentBillFetchRequest);
}

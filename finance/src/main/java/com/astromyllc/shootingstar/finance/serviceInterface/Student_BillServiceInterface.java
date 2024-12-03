package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.StudentBillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;

import java.util.List;

public interface Student_BillServiceInterface {
    public List<Student_BillResponse> createStudentsBill(List<Student_BillRequest> studentBillRequest);

    public Student_BillResponse createStudentBill(Student_BillRequest studentBillRequest);

    public List<Student_BillResponse> fetchStudentBillsByInstitution(StudentBillFetchRequest studentBillFetchRequest);

    public Student_BillResponse fetchStudentBillByIdAndInstitution(StudentBillFetchRequest studentBillFetchRequest);

    public List<Student_BillResponse> fetchOwingStudentsByInstitution(StudentBillFetchRequest studentBillFetchRequest);
}

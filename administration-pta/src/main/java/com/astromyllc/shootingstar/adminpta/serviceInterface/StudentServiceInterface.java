package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.request.*;
import com.astromyllc.shootingstar.adminpta.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSkimResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;

import java.util.List;
import java.util.Optional;

public interface StudentServiceInterface {

    void fetchCurrentApplications(AdmissionRequest admissionRequest);

    Optional<List<StudentsResponse>> fetchAllStudents();

    Optional<List<StudentsResponse>> fetchStudentsByClass(ClassListRequest request);

    Optional<List<StudentSkimResponse>> fetchSkimpStudentsByClass(ClassListRequest request);

    Optional<List<StudentsResponse>> postBulkStudentList(List<StudentsImportRequest> request);

    Optional<List<ClassListResponse>>  fetchAssessmentList(ClassListRequest request);

    Optional<List<StudentsResponse>>  fetchStudentsByStatus(SingleStringRequest status);

    Optional<List<StudentsResponse>>  fetchStudentsByInstitution(SingleStringRequest institution);
}

package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.request.AdmissionRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentSkimRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Students;

import java.util.List;
import java.util.Optional;

public interface StudentServiceInterface {

    public void fetchCurrentApplications(AdmissionRequest admissionRequest);

    List<StudentsResponse> fetchAllStudents();

    List<StudentsResponse> fetchStudentsByClass(StudentSkimRequest request);
}

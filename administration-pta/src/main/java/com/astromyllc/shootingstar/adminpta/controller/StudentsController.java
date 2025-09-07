package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.dto.request.*;
import com.astromyllc.shootingstar.adminpta.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSkimResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSkimWithParentResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.serviceInterface.StudentServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentsController {
    private final StudentServiceInterface studentServiceInterface;

    @PostMapping("/api/administration-pta/conduct-admissions")
    @ResponseStatus(HttpStatus.OK)
    public void conductAdmissions(@RequestBody AdmissionRequest admissionRequest) {
        studentServiceInterface.fetchCurrentApplications(admissionRequest);
    }

    @PostMapping("/api/administration-pta/accept-admissions")
    @ResponseStatus(HttpStatus.OK)
    public void conductAdmissions(@RequestBody Students2Request admissionRequest) {
        studentServiceInterface.fetchCurrentApplications(admissionRequest);
    }

    @PostMapping("/api/administration-pta/getAllStudents")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<StudentsResponse>>> getAllStudents() {
      return ResponseEntity.ok(studentServiceInterface.fetchAllStudents());
    }

    @PostMapping("/api/administration-pta/getStudentsByInstitution")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<StudentsResponse>>> getStudentsByInstitution(@RequestBody SingleStringRequest institution) {
        return ResponseEntity.ok(studentServiceInterface.fetchStudentsByInstitution(institution));
    }

    @PostMapping("/api/administration-pta/getStudentsByDynamicData")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<StudentsResponse>>> getStudentsByDynamicData(@RequestBody DynamicStringRequest institution) {
        return ResponseEntity.ok(studentServiceInterface.fetchStudentsByDynamicData(institution));
    }

    @PostMapping("/api/administration-pta/getStudentsByStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<StudentsResponse>>> getStudentsByStatus(@RequestBody SingleStringRequest status) {
        return ResponseEntity.ok(studentServiceInterface.fetchStudentsByStatus(status));
    }

    @PostMapping("/api/administration-pta/getAssessmentList")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<ClassListResponse>>> getAssessmentList(@RequestBody ClassListRequest request) {
        return ResponseEntity.ok(studentServiceInterface.fetchAssessmentList(request));
    }

    @PostMapping("/api/administration-pta/getStudentsByClass")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<StudentsResponse>>> getStudentsByClass(@RequestBody ClassListRequest request) {
        return ResponseEntity.ok(studentServiceInterface.fetchStudentsByClass(request));
    }

    @PostMapping("/api/administration-pta/getSkimpStudentsByClass")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<StudentSkimResponse>>> getSkimpStudentsByClass(@RequestBody ClassListRequest request) {
        return ResponseEntity.ok(studentServiceInterface.fetchSkimpStudentsByClass(request));
    }

    @PostMapping("/api/administration-pta/postBulkStudentList")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<StudentsResponse>>> postBulkStudentList(@RequestBody List<StudentsImportRequest> request) {
        return ResponseEntity.ok(studentServiceInterface.postBulkStudentList(request));
    }

    @PostMapping("/api/administration-pta/getSkimpStudentsByParentContact")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<StudentSkimWithParentResponse>>> getSkimpStudentsByParentContact(@RequestBody SingleStringRequest request) {
        Optional<List<StudentSkimWithParentResponse>> responseList=studentServiceInterface.getSkimpStudentsByParentContact(request);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/api/administration-pta/getStudentByID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<StudentSkimResponse>> getStudentByID(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(studentServiceInterface.getStudentByID(request));
    }

    @PostMapping("/api/administration-pta/getInstitutionPopulationByCode")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<Long>> getInstitutionPopulationByCode(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(studentServiceInterface.getStudentsPopulationByInstitution(request));
    }
}

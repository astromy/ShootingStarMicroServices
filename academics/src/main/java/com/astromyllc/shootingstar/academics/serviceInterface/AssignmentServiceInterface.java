package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentResponse;

import java.util.List;
import java.util.Optional;

public interface AssignmentServiceInterface {

    Optional<AssignmentResponse> createAssignment(AssignmentRequest request);

    Optional<AssignmentResponse> updateAssignment(AssignmentRequest request);

    void deleteAssignment(AssignmentRequest request);

    List<Optional<AssignmentResponse>> fetchAssignmentsByInstitution(AssignmentRequest request);

    List<Optional<AssignmentResponse>> fetchAssignmentsByClass(AssignmentRequest request);

    List<Optional<AssignmentResponse>> fetchAssignmentsByStaff(AssignmentRequest request);

    List<Optional<AssignmentResponse>> fetchAssignmentsByClassAndSubject(AssignmentRequest request);
}
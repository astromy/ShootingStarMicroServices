package com.astromyllc.shootingstar.academics.controller;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentResponse;
import com.astromyllc.shootingstar.academics.serviceInterface.AssignmentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/academics/")
public class AssignmentController {

    private final AssignmentServiceInterface assignmentServiceInterface;

    @PostMapping("createAssignment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Optional<AssignmentResponse>> createAssignment(
            @RequestBody AssignmentRequest request) {
        log.info("Creating assignment: {} for class {}", request.getTitle(), request.getClassId());
        return ResponseEntity.ok(assignmentServiceInterface.createAssignment(request));
    }

    @PostMapping("updateAssignment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<AssignmentResponse>> updateAssignment(
            @RequestBody AssignmentRequest request) {
        log.info("Updating assignment id={}", request.getId());
        return ResponseEntity.ok(assignmentServiceInterface.updateAssignment(request));
    }

    @PostMapping("deleteAssignment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteAssignment(
            @RequestBody AssignmentRequest request) {
        assignmentServiceInterface.deleteAssignment(request);
        return ResponseEntity.ok("Assignment deleted successfully");
    }

    @PostMapping("fetchAssignmentsByInstitution")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Optional<AssignmentResponse>>> fetchAssignmentsByInstitution(
            @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentServiceInterface.fetchAssignmentsByInstitution(request));
    }

    @PostMapping("fetchAssignmentsByClass")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Optional<AssignmentResponse>>> fetchAssignmentsByClass(
            @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentServiceInterface.fetchAssignmentsByClass(request));
    }

    @PostMapping("fetchAssignmentsByStaff")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Optional<AssignmentResponse>>> fetchAssignmentsByStaff(
            @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentServiceInterface.fetchAssignmentsByStaff(request));
    }

    @PostMapping("fetchAssignmentsByClassAndSubject")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Optional<AssignmentResponse>>> fetchAssignmentsByClassAndSubject(
            @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentServiceInterface.fetchAssignmentsByClassAndSubject(request));
    }
}
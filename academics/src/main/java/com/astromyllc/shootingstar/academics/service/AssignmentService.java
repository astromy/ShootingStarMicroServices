package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentResponse;
import com.astromyllc.shootingstar.academics.model.Assignment;
import com.astromyllc.shootingstar.academics.repository.AssignmentRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.AssignmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.AssignmentUtil;
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
public class AssignmentService implements AssignmentServiceInterface {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentUtil assignmentUtil;

    @Override
    public Optional<AssignmentResponse> createAssignment(AssignmentRequest request) {
        Assignment assignment = assignmentUtil.mapRequestToAssignment(request);
        assignmentRepository.save(assignment);
        AssignmentUtil.assignmentGlobalList.add(assignment);
        log.info("Assignment created: {} for class {}", assignment.getTitle(), assignment.getClassId());
        return Optional.of(assignmentUtil.mapAssignmentToResponse(assignment));
    }

    @Override
    public Optional<AssignmentResponse> updateAssignment(AssignmentRequest request) {
        return AssignmentUtil.assignmentGlobalList.stream()
                .filter(a -> a.getId().equals(request.getId()))
                .findFirst()
                .map(existing -> {
                    existing.setTitle(request.getTitle());
                    existing.setDeliveryMode(request.getDeliveryMode());
                    existing.setSelectionMode(request.getSelectionMode());
                    existing.setQuestionCount(request.getQuestionCount());
                    existing.setStatus(request.getStatus());
                    if (request.getDeadline() != null) {
                        existing.setDeadline(java.time.LocalDateTime.parse(
                                request.getDeadline(),
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                        ));
                    }
                    assignmentRepository.save(existing);
                    return assignmentUtil.mapAssignmentToResponse(existing);
                });
    }

    @Override
    public void deleteAssignment(AssignmentRequest request) {
        AssignmentUtil.assignmentGlobalList.stream()
                .filter(a -> a.getId().equals(request.getId()))
                .findFirst()
                .ifPresent(existing -> {
                    assignmentRepository.delete(existing);
                    AssignmentUtil.assignmentGlobalList.remove(existing);
                    log.info("Assignment deleted: id={}", existing.getId());
                });
    }

    @Override
    public List<Optional<AssignmentResponse>> fetchAssignmentsByInstitution(AssignmentRequest request) {
        return AssignmentUtil.assignmentGlobalList.stream()
                .filter(a -> a.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode()))
                .map(a -> Optional.of(assignmentUtil.mapAssignmentToResponse(a)))
                .toList();
    }

    @Override
    public List<Optional<AssignmentResponse>> fetchAssignmentsByClass(AssignmentRequest request) {
        return AssignmentUtil.assignmentGlobalList.stream()
                .filter(a -> a.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())
                        && a.getClassId().equalsIgnoreCase(request.getClassId()))
                .map(a -> Optional.of(assignmentUtil.mapAssignmentToResponse(a)))
                .toList();
    }

    @Override
    public List<Optional<AssignmentResponse>> fetchAssignmentsByStaff(AssignmentRequest request) {
        return AssignmentUtil.assignmentGlobalList.stream()
                .filter(a -> a.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())
                        && a.getStaffId().equalsIgnoreCase(request.getStaffId()))
                .map(a -> Optional.of(assignmentUtil.mapAssignmentToResponse(a)))
                .toList();
    }

    @Override
    public List<Optional<AssignmentResponse>> fetchAssignmentsByClassAndSubject(AssignmentRequest request) {
        return AssignmentUtil.assignmentGlobalList.stream()
                .filter(a -> a.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())
                        && a.getClassId().equalsIgnoreCase(request.getClassId())
                        && a.getSubjectId().equalsIgnoreCase(request.getSubjectId()))
                .map(a -> Optional.of(assignmentUtil.mapAssignmentToResponse(a)))
                .toList();
    }
}
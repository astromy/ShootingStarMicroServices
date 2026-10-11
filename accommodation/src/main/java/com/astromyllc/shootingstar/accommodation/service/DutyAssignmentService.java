package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.CreateDutyRotationRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.UpdateDutyStatusRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.DutyAssignmentResponse;
import com.astromyllc.shootingstar.accommodation.model.Block;
import com.astromyllc.shootingstar.accommodation.model.DutyAssignment;
import com.astromyllc.shootingstar.accommodation.model.DutyType;
import com.astromyllc.shootingstar.accommodation.repository.BlockRepository;
import com.astromyllc.shootingstar.accommodation.repository.DutyAssignmentRepository;
import com.astromyllc.shootingstar.accommodation.repository.DutyTypeRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.DutyAssignmentServiceInterface;
import com.astromyllc.shootingstar.accommodation.util.AccommodationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DutyAssignmentService implements DutyAssignmentServiceInterface {

    private final DutyAssignmentRepository dutyAssignmentRepository;
    private final BlockRepository blockRepository;
    private final DutyTypeRepository dutyTypeRepository;

    private static final int MAX_WEEKS = 52;

    @Override
    public List<DutyAssignmentResponse> createRotation(CreateDutyRotationRequest request) {
        if (request.getIdBlock() == null || request.getIdDutyType() == null
                || request.getStudentId() == null || request.getStudentId().isBlank()) {
            throw new IllegalArgumentException("idBlock, idDutyType and studentId are required");
        }
        if (request.getStartWeek() == null) {
            throw new IllegalArgumentException("startWeek is required");
        }
        int weeks = request.getNumberOfWeeks() == null ? 1 : request.getNumberOfWeeks();
        if (weeks < 1 || weeks > MAX_WEEKS) {
            throw new IllegalArgumentException("numberOfWeeks must be between 1 and " + MAX_WEEKS);
        }

        Block block = blockRepository.findById(request.getIdBlock())
                .orElseThrow(() -> new IllegalArgumentException("No block found with id " + request.getIdBlock()));
        DutyType dutyType = dutyTypeRepository.findById(request.getIdDutyType())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No duty type found with id " + request.getIdDutyType()));

        String rotationId = UUID.randomUUID().toString();
        List<DutyAssignment> generated = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            generated.add(DutyAssignment.builder()
                    .institutionCode(request.getInstitutionCode())
                    .idBlock(request.getIdBlock())
                    .idDutyType(request.getIdDutyType())
                    .studentId(request.getStudentId())
                    .studentName(request.getStudentName())
                    .weekStartDate(request.getStartWeek().plusWeeks(i))
                    .status("PENDING")
                    .rotationId(rotationId)
                    .build());
        }

        List<DutyAssignment> saved = dutyAssignmentRepository.saveAll(generated);
        List<DutyAssignmentResponse> responses = new ArrayList<>();
        for (DutyAssignment a : saved) {
            responses.add(AccommodationMapper.mapDutyAssignmentToResponse(a, block.getName(), dutyType.getName()));
        }
        return responses;
    }

    @Override
    public DutyAssignmentResponse updateStatus(UpdateDutyStatusRequest request) {
        if (request.getIdDutyAssignment() == null || request.getStatus() == null) {
            throw new IllegalArgumentException("idDutyAssignment and status are required");
        }
        DutyAssignment assignment = dutyAssignmentRepository.findById(request.getIdDutyAssignment())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No duty assignment found with id " + request.getIdDutyAssignment()));
        assignment.setStatus(request.getStatus());
        DutyAssignment saved = dutyAssignmentRepository.save(assignment);
        return enrich(saved);
    }

    @Override
    public List<DutyAssignmentResponse> getBlockRoster(Long idBlock) {
        if (idBlock == null) return new ArrayList<>();
        List<DutyAssignment> assignments = dutyAssignmentRepository.findByIdBlock(idBlock);
        List<DutyAssignmentResponse> responses = new ArrayList<>();
        for (DutyAssignment a : assignments) {
            responses.add(enrich(a));
        }
        return responses;
    }

    @Override
    public boolean cancelRotation(String rotationId) {
        if (rotationId == null || rotationId.isBlank()) return false;
        List<DutyAssignment> rows = dutyAssignmentRepository.findByRotationId(rotationId);
        if (rows.isEmpty()) return false;
        // Only future/pending weeks are removed - completed or missed weeks
        // stay as history rather than being erased by a cancellation.
        List<DutyAssignment> toDelete = rows.stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .toList();
        dutyAssignmentRepository.deleteAll(toDelete);
        return true;
    }

    @Override
    public boolean deleteAssignment(Long idDutyAssignment) {
        if (idDutyAssignment == null || !dutyAssignmentRepository.existsById(idDutyAssignment)) {
            return false;
        }
        dutyAssignmentRepository.deleteById(idDutyAssignment);
        return true;
    }

    private DutyAssignmentResponse enrich(DutyAssignment assignment) {
        String blockName = blockRepository.findById(assignment.getIdBlock())
                .map(Block::getName).orElse(null);
        String dutyTypeName = dutyTypeRepository.findById(assignment.getIdDutyType())
                .map(DutyType::getName).orElse(null);
        return AccommodationMapper.mapDutyAssignmentToResponse(assignment, blockName, dutyTypeName);
    }
}

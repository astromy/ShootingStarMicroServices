package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.config.StudentNotEligibleException;
import com.astromyllc.shootingstar.adminpta.dto.request.GateCheckRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.GateEventResponse;
import com.astromyllc.shootingstar.adminpta.model.GateEvent;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.repository.GateEventRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.GateEventServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.GateEventUtil;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GateEventService implements GateEventServiceInterface {

    private final GateEventRepository gateEventRepository;

    @Override
    public GateEventResponse recordGateEvent(GateCheckRequest request) throws StudentNotEligibleException {
        Optional<Students> student = StudentUtil.studentsGlobalList.parallelStream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(request.getStudentId()))
                .findFirst();

        if (student.isEmpty()) {
            log.warn("Gate event rejected: no student found for studentId {}", request.getStudentId());
            throw new StudentNotEligibleException("No student record found for this ID.");
        }
        if (!student.get().getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())) {
            log.warn("Gate event rejected: student {} does not belong to institution {}",
                    request.getStudentId(), request.getInstitutionCode());
            throw new StudentNotEligibleException("This student isn't registered to this school.");
        }

        GateEvent event = GateEventUtil.mapRequest_ToGateEvent(request);
        gateEventRepository.save(event);
        log.info("Gate event recorded: student {} {} at institution {} (by {})",
                request.getStudentId(), request.getType(), request.getInstitutionCode(), request.getRecordedBy());

        return GateEventUtil.mapGateEvent_ToGateEventResponse(event);
    }
}

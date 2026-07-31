package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.config.StudentNotEligibleException;
import com.astromyllc.shootingstar.adminpta.dto.request.BusBoardingRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.BusBoardingResponse;
import com.astromyllc.shootingstar.adminpta.model.BusBoardingEvent;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.repository.BusBoardingEventRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.BusBoardingEventServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.BusBoardingEventUtil;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusBoardingEventService implements BusBoardingEventServiceInterface {

    private final BusBoardingEventRepository busBoardingEventRepository;

    @Override
    public BusBoardingResponse recordBusBoardingEvent(BusBoardingRequest request) throws StudentNotEligibleException {
        Optional<Students> studentOpt = StudentUtil.studentsGlobalList.parallelStream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(request.getStudentId()))
                .findFirst();

        if (studentOpt.isEmpty()) {
            log.warn("Bus boarding event rejected: no student found for studentId {}", request.getStudentId());
            throw new StudentNotEligibleException("No student record found for this ID.");
        }
        Students student = studentOpt.get();
        if (!student.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())) {
            log.warn("Bus boarding event rejected: student {} does not belong to institution {}",
                    request.getStudentId(), request.getInstitutionCode());
            throw new StudentNotEligibleException("This student isn't registered to this school.");
        }

        boolean routeMismatch = BusBoardingEventUtil.isRouteMismatch(student.getAssignedRouteId(), request.getRouteId());
        if (routeMismatch) {
            log.warn("Route mismatch: student {} (route {}) {} bus on route {}",
                    request.getStudentId(), student.getAssignedRouteName(), request.getType(), request.getRouteName());
        }

        BusBoardingEvent event = BusBoardingEventUtil.mapRequest_ToBusBoardingEvent(
                request, routeMismatch, student.getAssignedRouteName());
        busBoardingEventRepository.save(event);
        log.info("Bus boarding event recorded: student {} {} on bus {} at institution {} (by {})",
                request.getStudentId(), request.getType(), request.getBusName(),
                request.getInstitutionCode(), request.getRecordedBy());

        return BusBoardingEventUtil.mapBusBoardingEvent_ToBusBoardingResponse(event);
    }
}
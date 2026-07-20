package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.StaffClockInRequest;
import com.astromyllc.shootingstar.hr.dto.response.ClockEventResponse;
import com.astromyllc.shootingstar.hr.model.ClockEvent;
import com.astromyllc.shootingstar.hr.model.Staff;
import com.astromyllc.shootingstar.hr.repository.ClockEventRepository;
import com.astromyllc.shootingstar.hr.serviceInterface.ClockEventServiceInterface;
import com.astromyllc.shootingstar.hr.utils.ClockEventUtil;
import com.astromyllc.shootingstar.hr.utils.StaffUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClockEventService implements ClockEventServiceInterface {

    private final ClockEventRepository clockEventRepository;

    @Override
    public Optional<ClockEventResponse> recordClockEvent(StaffClockInRequest request) {
        Optional<Staff> staff = StaffUtil.staffGlobalList.stream()
                .filter(s -> s.getStaffCode().equalsIgnoreCase(request.getStaffId()))
                .findFirst();

        if (staff.isEmpty()) {
            log.warn("Clock event rejected: no staff found for staffCode {}", request.getStaffId());
            return Optional.empty();
        }
        if (!staff.get().getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())) {
            log.warn("Clock event rejected: staff {} does not belong to institution {}",
                    request.getStaffId(), request.getInstitutionCode());
            return Optional.empty();
        }

        ClockEvent event = ClockEventUtil.mapRequest_ToClockEvent(request);
        clockEventRepository.save(event);
        log.info("Clock event recorded: staff {} {} at institution {}",
                request.getStaffId(), request.getType(), request.getInstitutionCode());

        return Optional.of(ClockEventUtil.mapClockEvent_ToClockEventResponse(event));
    }
}
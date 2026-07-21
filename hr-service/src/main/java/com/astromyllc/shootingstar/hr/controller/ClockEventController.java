package com.astromyllc.shootingstar.hr.controller;

import com.astromyllc.shootingstar.hr.config.StaffNotEligibleException;
import com.astromyllc.shootingstar.hr.dto.request.StaffClockInRequest;
import com.astromyllc.shootingstar.hr.dto.response.ClockEventResponse;
import com.astromyllc.shootingstar.hr.serviceInterface.ClockEventServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClockEventController {

    private final ClockEventServiceInterface clockEventServiceInterface;

    @PostMapping
    @RequestMapping("/api/hr/staffClockIn")
    public ResponseEntity<?> staffClockIn(@RequestBody StaffClockInRequest request) {
        log.info("Recording clock event for staff {}", request.getStaffId());
        try {
            ClockEventResponse response = clockEventServiceInterface.recordClockEvent(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (StaffNotEligibleException e) {
            // Message is written to be shown to the user as-is — apiService.js
            // on the mobile side reads error.response.data.message directly.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
}
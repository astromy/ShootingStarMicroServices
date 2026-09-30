package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.config.StudentNotEligibleException;
import com.astromyllc.shootingstar.adminpta.dto.request.AttendanceStatsRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.MarkAttendanceRequest;
import com.astromyllc.shootingstar.adminpta.serviceInterface.AttendanceServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceServiceInterface attendanceServiceInterface;

    // Error bodies are { message }, which the mobile apps show to the user as-is.
    @PostMapping("/api/administration-pta/markAttendance")
    public ResponseEntity<?> markAttendance(@RequestBody MarkAttendanceRequest request) {
        log.info("Marking attendance for student {}", request.getStudentId());
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(attendanceServiceInterface.markAttendance(request));
        } catch (StudentNotEligibleException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/api/administration-pta/getAttendanceStats")
    public ResponseEntity<?> getAttendanceStats(@RequestBody AttendanceStatsRequest request) {
        try {
            return ResponseEntity.ok(attendanceServiceInterface.getAttendanceStats(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}

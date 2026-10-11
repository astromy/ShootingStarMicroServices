package com.astromyllc.shootingstar.hr.controller;

import com.astromyllc.shootingstar.hr.dto.request.AttendanceQueryRequest;
import com.astromyllc.shootingstar.hr.dto.request.ClockWebRequest;
import com.astromyllc.shootingstar.hr.dto.request.LeaveApplyRequest;
import com.astromyllc.shootingstar.hr.dto.request.LeaveDecisionRequest;
import com.astromyllc.shootingstar.hr.dto.request.LeaveEntitlementRequest;
import com.astromyllc.shootingstar.hr.dto.request.LeaveQueryRequest;
import com.astromyllc.shootingstar.hr.dto.response.AttendanceRow;
import com.astromyllc.shootingstar.hr.dto.response.LeaveEntitlementResponse;
import com.astromyllc.shootingstar.hr.dto.response.LeaveResponse;
import com.astromyllc.shootingstar.hr.service.AttendanceService;
import com.astromyllc.shootingstar.hr.service.LeaveEntitlementService;
import com.astromyllc.shootingstar.hr.service.LeaveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** The endpoints the Leave screen (_hrLeave.js) has always called. */
@RestController
public class LeaveController {

    private final LeaveService leaveService;
    private final AttendanceService attendanceService;
    private final LeaveEntitlementService entitlementService;

    public LeaveController(LeaveService leaveService, AttendanceService attendanceService,
                           LeaveEntitlementService entitlementService) {
        this.leaveService = leaveService;
        this.attendanceService = attendanceService;
        this.entitlementService = entitlementService;
    }

    @PostMapping("/api/hr/getLeaveEntitlements")
    public List<LeaveEntitlementResponse> getLeaveEntitlements(@RequestBody LeaveEntitlementRequest request) {
        return entitlementService.get(request.institutionCode());
    }

    @PostMapping("/api/hr/saveLeaveEntitlements")
    public List<LeaveEntitlementResponse> saveLeaveEntitlements(@RequestBody LeaveEntitlementRequest request) {
        return entitlementService.save(request);
    }

    @PostMapping("/api/hr/applyLeave")
    public LeaveResponse applyLeave(@RequestBody LeaveApplyRequest request) {
        return leaveService.apply(request);
    }

    @PostMapping("/api/hr/approveLeave")
    public LeaveResponse approveLeave(@RequestBody LeaveDecisionRequest request) {
        return leaveService.decide(request);
    }

    @PostMapping("/api/hr/getLeaveRequests")
    public List<LeaveResponse> getLeaveRequests(@RequestBody LeaveQueryRequest request) {
        return leaveService.list(request);
    }

    @PostMapping("/api/hr/getLeaveBalance")
    public Map<String, Integer> getLeaveBalance(@RequestBody LeaveQueryRequest request) {
        return leaveService.balance(request);
    }

    @PostMapping("/api/hr/checkInEmployee")
    public AttendanceRow checkInEmployee(@RequestBody ClockWebRequest request) {
        return attendanceService.checkIn(request);
    }

    @PostMapping("/api/hr/checkOutEmployee")
    public AttendanceRow checkOutEmployee(@RequestBody ClockWebRequest request) {
        return attendanceService.checkOut(request);
    }

    @PostMapping("/api/hr/getEmployeeAttendance")
    public List<AttendanceRow> getEmployeeAttendance(@RequestBody AttendanceQueryRequest request) {
        return attendanceService.attendance(request);
    }
}

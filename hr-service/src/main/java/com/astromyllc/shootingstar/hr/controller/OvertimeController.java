package com.astromyllc.shootingstar.hr.controller;

import com.astromyllc.shootingstar.hr.dto.request.ManHoursRequest;
import com.astromyllc.shootingstar.hr.dto.request.OvertimeDecisionRequest;
import com.astromyllc.shootingstar.hr.dto.request.OvertimeDetectRequest;
import com.astromyllc.shootingstar.hr.dto.request.OvertimePayrollRequest;
import com.astromyllc.shootingstar.hr.dto.request.OvertimeQueryRequest;
import com.astromyllc.shootingstar.hr.dto.response.CountResponse;
import com.astromyllc.shootingstar.hr.dto.response.DetectResponse;
import com.astromyllc.shootingstar.hr.dto.response.ManHoursRow;
import com.astromyllc.shootingstar.hr.dto.response.OvertimePayrollBatch;
import com.astromyllc.shootingstar.hr.dto.response.OvertimeResponse;
import com.astromyllc.shootingstar.hr.service.ManHoursService;
import com.astromyllc.shootingstar.hr.service.OvertimeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OvertimeController {

    private final OvertimeService overtimeService;
    private final ManHoursService manHoursService;

    public OvertimeController(OvertimeService overtimeService, ManHoursService manHoursService) {
        this.overtimeService = overtimeService;
        this.manHoursService = manHoursService;
    }

    @PostMapping("/api/hr/detectOvertime")
    public DetectResponse detectOvertime(@RequestBody OvertimeDetectRequest request) {
        return overtimeService.detect(request);
    }

    @PostMapping("/api/hr/getOvertime")
    public List<OvertimeResponse> getOvertime(@RequestBody OvertimeQueryRequest request) {
        return overtimeService.list(request);
    }

    @PostMapping("/api/hr/approveOvertime")
    public List<OvertimeResponse> approveOvertime(@RequestBody OvertimeDecisionRequest request) {
        return overtimeService.approve(request);
    }

    @PostMapping("/api/hr/rejectOvertime")
    public List<OvertimeResponse> rejectOvertime(@RequestBody OvertimeDecisionRequest request) {
        return overtimeService.reject(request);
    }

    // The three below are called by Finance's payroll run, not by the browser.

    @PostMapping("/api/hr/getApprovedOvertimeForPayroll")
    public OvertimePayrollBatch getApprovedOvertimeForPayroll(@RequestBody OvertimePayrollRequest request) {
        return overtimeService.payrollBatch(request);
    }

    @PostMapping("/api/hr/lockOvertimeForPayroll")
    public CountResponse lockOvertimeForPayroll(@RequestBody OvertimePayrollRequest request) {
        return overtimeService.lockForPayroll(request);
    }

    @PostMapping("/api/hr/releaseOvertimeForPayroll")
    public CountResponse releaseOvertimeForPayroll(@RequestBody OvertimePayrollRequest request) {
        return overtimeService.releaseFromPayroll(request);
    }

    @PostMapping("/api/hr/getManHours")
    public List<ManHoursRow> getManHours(@RequestBody ManHoursRequest request) {
        return manHoursService.monthly(request);
    }
}

package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.SalaryFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalarySettingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.SalaryResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalarySettingsResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.SalaryServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Slf4j
public class SalaryController {

    private final SalaryServiceInterface salaryServiceInterface;

    // ── Settings ─────────────────────────────────────────────────────────────

    @PostMapping("/salary-settings/save")
    @ResponseStatus(HttpStatus.CREATED)
    public SalarySettingsResponse saveSettings(@RequestBody SalarySettingsRequest request) {
        log.info("Saving salary settings for institution {}", request.getInstitutionCode());
        return salaryServiceInterface.saveSettings(request);
    }

    @PostMapping("/salary-settings/get")
    @ResponseStatus(HttpStatus.OK)
    public Optional<SalarySettingsResponse> getSettings(@RequestBody SalaryFetchRequest request) {
        return salaryServiceInterface.getSettings(request.getInstitutionCode());
    }

    // ── Salary run creation ───────────────────────────────────────────────────

    /**
     * Create one salary run for one staff member
     */
    @PostMapping("/salary/create")
    @ResponseStatus(HttpStatus.CREATED)
    public SalaryResponse createSalary(@RequestBody SalaryRequest request) {
        log.info("Creating salary for staff {} period {}", request.getStaffId(), request.getPayPeriod());
        return salaryServiceInterface.createSalary(request);
    }

    /**
     * Run payroll for a list of staff in one call
     */
    @PostMapping("/salary/create-batch")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SalaryResponse> createSalaries(@RequestBody List<SalaryRequest> requests) {
        log.info("Batch salary creation: {} staff members", requests.size());
        return salaryServiceInterface.createSalaries(requests);
    }

    // ── Status transitions ────────────────────────────────────────────────────

    /**
     * Approve a salary run (PENDING → APPROVED)
     */
    @PostMapping("/salary/approve/{salaryId}")
    @ResponseStatus(HttpStatus.OK)
    public SalaryResponse approveSalary(
            @PathVariable Long salaryId,
            @RequestParam String approvedBy) {
        return salaryServiceInterface.approveSalary(salaryId, approvedBy);
    }

    /**
     * Mark a salary as paid (APPROVED → PAID)
     */
    @PostMapping("/salary/mark-paid/{salaryId}")
    @ResponseStatus(HttpStatus.OK)
    public SalaryResponse markPaid(
            @PathVariable Long salaryId,
            @RequestParam String processedBy,
            @RequestParam(required = false) String externalReference) {
        return salaryServiceInterface.markPaid(salaryId, processedBy, externalReference);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /**
     * All salary runs for an institution, filterable by year / period / status / staff
     */
    @PostMapping("/salary/get-by-institution")
    @ResponseStatus(HttpStatus.OK)
    public List<SalaryResponse> getByInstitution(@RequestBody SalaryFetchRequest request) {
        return salaryServiceInterface.getSalariesByInstitution(request);
    }

    @GetMapping("/salary/get/{salaryId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<SalaryResponse> getById(@PathVariable Long salaryId) {
        return salaryServiceInterface.getSalaryById(salaryId);
    }

    /**
     * Payslip endpoint — returns the salary record for one staff, one period
     */
    @PostMapping("/salary/payslip")
    @ResponseStatus(HttpStatus.OK)
    public Optional<SalaryResponse> getPayslip(@RequestBody SalaryFetchRequest request) {
        return salaryServiceInterface.getPayslip(
                request.getStaffId(),
                request.getInstitutionCode(),
                request.getPayPeriod(),
                request.getAcademicYear()
        );
    }
}
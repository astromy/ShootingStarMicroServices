package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.PayrollActionRequest;
import com.astromyllc.shootingstar.finance.dto.request.RunPayrollRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryProfileRequest;
import com.astromyllc.shootingstar.finance.dto.response.PayrollActionResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalaryProfileResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.PayrollServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Salary profiles, payroll runs and bulk approval / payment.
 * institutionCode and the acting user are always set by the gateway.
 * Errors come back as { message } - see PayrollExceptionHandler.
 */
@RestController
@RequestMapping("/api/finance/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollServiceInterface payrollServiceInterface;

    @PostMapping("/profiles/get")
    public List<SalaryProfileResponse> getProfiles(@RequestBody SalaryFetchRequest request) {
        return payrollServiceInterface.getProfiles(request.getInstitutionCode());
    }

    @PostMapping("/profiles/save")
    public SalaryProfileResponse saveProfile(@RequestBody SalaryProfileRequest request) {
        return payrollServiceInterface.saveProfile(request);
    }

    @PostMapping("/run")
    public PayrollActionResponse runPayroll(@RequestBody RunPayrollRequest request) {
        return payrollServiceInterface.runPayroll(request);
    }

    @PostMapping("/approve")
    public PayrollActionResponse approve(@RequestBody PayrollActionRequest request) {
        return payrollServiceInterface.approve(request);
    }

    @PostMapping("/mark-paid")
    public PayrollActionResponse markPaid(@RequestBody PayrollActionRequest request) {
        return payrollServiceInterface.markPaid(request);
    }

    @PostMapping("/delete-pending")
    public PayrollActionResponse deletePending(@RequestBody PayrollActionRequest request) {
        return payrollServiceInterface.deletePending(request);
    }
}

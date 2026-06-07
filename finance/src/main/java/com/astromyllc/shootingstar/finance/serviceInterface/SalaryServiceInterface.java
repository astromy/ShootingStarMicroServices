package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.SalaryFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalarySettingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.SalaryResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalarySettingsResponse;

import java.util.List;
import java.util.Optional;

public interface SalaryServiceInterface {
    public SalarySettingsResponse saveSettings(SalarySettingsRequest r);

    public Optional<SalarySettingsResponse> getSettings(String institutionCode);

    public SalaryResponse createSalary(SalaryRequest r);

    public List<SalaryResponse> createSalaries(List<SalaryRequest> requests);

    public SalaryResponse approveSalary(Long salaryId, String approvedBy);

    public SalaryResponse markPaid(Long salaryId, String processedBy, String externalReference);

    public List<SalaryResponse> getSalariesByInstitution(SalaryFetchRequest r);

    public Optional<SalaryResponse> getSalaryById(Long salaryId);

    public Optional<SalaryResponse> getPayslip(String staffId, String institutionCode,
                                               String payPeriod, String academicYear);
}

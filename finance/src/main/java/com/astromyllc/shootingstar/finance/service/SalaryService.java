package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.SalaryFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalarySettingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.SalaryResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalarySettingsResponse;
import com.astromyllc.shootingstar.finance.model.Salaries;
import com.astromyllc.shootingstar.finance.model.SalarySettings;
import com.astromyllc.shootingstar.finance.repositoy.SalariesRepository;
import com.astromyllc.shootingstar.finance.repositoy.SalarySettingsRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.SalaryServiceInterface;
import com.astromyllc.shootingstar.finance.utils.SalaryUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalaryService implements SalaryServiceInterface {

    private final SalariesRepository salariesRepository;
    private final SalarySettingsRepository salarySettingsRepository;
    private final SalaryUtil salaryUtil;

    // ── Settings ────────────────────────────────────────────────────────────

    @Override
    public SalarySettingsResponse saveSettings(SalarySettingsRequest r) {
        Optional<SalarySettings> existing = SalaryUtil.salarySettingsGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                .findFirst();

        SalarySettings saved;
        if (existing.isPresent()) {
            SalarySettings s = existing.get();
            s.setSsnitEmployeeRate(r.getSsnitEmployeeRate());
            s.setSsnitEmployerRate(r.getSsnitEmployerRate());
            s.setIncomeTaxBands(r.getIncomeTaxBands());
            if (r.getDefaultAllowances() != null) {
                s.getDefaultAllowances().clear();
                s.getDefaultAllowances().addAll(
                        salaryUtil.mapSettingsRequest_ToSettings(r).getDefaultAllowances()
                );
            }
            saved = salarySettingsRepository.save(s);
            int idx = SalaryUtil.salarySettingsGlobalList.indexOf(existing.get());
            if (idx >= 0) SalaryUtil.salarySettingsGlobalList.set(idx, saved);
        } else {
            saved = salaryUtil.mapSettingsRequest_ToSettings(r);
            salarySettingsRepository.save(saved);
            SalaryUtil.salarySettingsGlobalList.add(saved);
        }
        return salaryUtil.mapSettings_ToResponse(saved);
    }

    @Override
    public Optional<SalarySettingsResponse> getSettings(String institutionCode) {
        return SalaryUtil.salarySettingsGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(institutionCode))
                .map(salaryUtil::mapSettings_ToResponse)
                .findFirst();
    }

    // ── Salary run creation ─────────────────────────────────────────────────

    /**
     * Create a salary run for one staff member.
     * If settings exist for the institution, seed the default allowances/deductions
     * into the run before adding any request-specific items.
     */
    @Override
    public SalaryResponse createSalary(SalaryRequest r) {
        // Seed from institution settings if no items supplied in request
        if ((r.getSalaryItems() == null || r.getSalaryItems().isEmpty())) {
            SalaryUtil.salarySettingsGlobalList.stream()
                    .filter(s -> s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                    .findFirst()
                    .ifPresent(settings -> {
                        r.setSalaryItems(
                                settings.getDefaultAllowances().stream()
                                        .map(item -> com.astromyllc.shootingstar.finance.dto.request.SalaryItemRequest.builder()
                                                .itemName(item.getItemName())
                                                .itemType(item.getItemType())
                                                .amount(item.getAmount())
                                                .isPercentage(item.getIsPercentage())
                                                .percentageRate(item.getPercentageRate())
                                                .build())
                                        .collect(Collectors.toList())
                        );
                    });
        }

        Salaries salary = salaryUtil.mapRequest_ToSalary(r);
        salariesRepository.save(salary);
        SalaryUtil.salaryGlobalList.add(salary);
        log.info("Salary run created for staff {} period {} status PENDING", r.getStaffId(), r.getPayPeriod());
        return salaryUtil.mapSalary_ToResponse(salary);
    }

    /**
     * Bulk creation — one call to run payroll for entire staff list
     */
    @Override
    public List<SalaryResponse> createSalaries(List<SalaryRequest> requests) {
        return requests.stream()
                .map(this::createSalary)
                .collect(Collectors.toList());
    }

    // ── Status transitions ───────────────────────────────────────────────────

    @Override
    public SalaryResponse approveSalary(Long salaryId, String approvedBy) {
        Salaries salary = findByIdOrThrow(salaryId);
        salaryUtil.applyApproval(salary, approvedBy);
        salariesRepository.save(salary);
        syncGlobalList(salary);
        log.info("Salary {} approved by {}", salaryId, approvedBy);
        return salaryUtil.mapSalary_ToResponse(salary);
    }

    @Override
    public SalaryResponse markPaid(Long salaryId, String processedBy, String externalReference) {
        Salaries salary = findByIdOrThrow(salaryId);
        if (!"APPROVED".equalsIgnoreCase(salary.getStatus())) {
            throw new IllegalStateException("Salary must be APPROVED before marking as PAID. Current status: " + salary.getStatus());
        }
        salaryUtil.applyPayment(salary, processedBy, externalReference);
        salariesRepository.save(salary);
        syncGlobalList(salary);
        log.info("Salary {} marked PAID by {}", salaryId, processedBy);
        return salaryUtil.mapSalary_ToResponse(salary);
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    @Override
    public List<SalaryResponse> getSalariesByInstitution(SalaryFetchRequest r) {
        return SalaryUtil.salaryGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                .filter(s -> r.getAcademicYear() == null || s.getAcademicYear().equalsIgnoreCase(r.getAcademicYear()))
                .filter(s -> r.getPayPeriod() == null || s.getPayPeriod().equalsIgnoreCase(r.getPayPeriod()))
                .filter(s -> r.getStatus() == null || s.getStatus().equalsIgnoreCase(r.getStatus()))
                .filter(s -> r.getStaffId() == null || s.getStaffId().equalsIgnoreCase(r.getStaffId()))
                .map(salaryUtil::mapSalary_ToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SalaryResponse> getSalaryById(Long salaryId) {
        return SalaryUtil.salaryGlobalList.stream()
                .filter(s -> s.getSalaryId().equals(salaryId))
                .map(salaryUtil::mapSalary_ToResponse)
                .findFirst();
    }

    /**
     * Payslip — single staff, single pay period.
     * Returns the most recent PAID run for the given staff/period/year.
     */
    @Override
    public Optional<SalaryResponse> getPayslip(String staffId, String institutionCode,
                                               String payPeriod, String academicYear) {
        return SalaryUtil.salaryGlobalList.stream()
                .filter(s -> s.getStaffId().equalsIgnoreCase(staffId)
                        && s.getInstitutionCode().equalsIgnoreCase(institutionCode)
                        && s.getPayPeriod().equalsIgnoreCase(payPeriod)
                        && s.getAcademicYear().equalsIgnoreCase(academicYear))
                .map(salaryUtil::mapSalary_ToResponse)
                .findFirst();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private Salaries findByIdOrThrow(Long id) {
        return SalaryUtil.salaryGlobalList.stream()
                .filter(s -> s.getSalaryId().equals(id))
                .findFirst()
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Salary not found: " + id));
    }

    private void syncGlobalList(Salaries updated) {
        int idx = SalaryUtil.salaryGlobalList.stream()
                .map(Salaries::getSalaryId)
                .collect(Collectors.toList())
                .indexOf(updated.getSalaryId());
        if (idx >= 0) SalaryUtil.salaryGlobalList.set(idx, updated);
    }
}
package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.dto.request.SalaryItemRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalarySettingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.SalaryItemResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalaryResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalarySettingsResponse;
import com.astromyllc.shootingstar.finance.model.Salaries;
import com.astromyllc.shootingstar.finance.model.SalaryItem;
import com.astromyllc.shootingstar.finance.model.SalarySettings;
import com.astromyllc.shootingstar.finance.repositoy.SalariesRepository;
import com.astromyllc.shootingstar.finance.repositoy.SalarySettingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalaryUtil {

    public static List<Salaries> salaryGlobalList = new ArrayList<>();
    public static List<SalarySettings> salarySettingsGlobalList = new ArrayList<>();
    private final SalariesRepository salariesRepository;
    private final SalarySettingsRepository salarySettingsRepository;

    @PostConstruct
    private void loadAll() {
        salaryGlobalList = salariesRepository.findAll();
        salarySettingsGlobalList = salarySettingsRepository.findAll();
        log.info("Global Salary list populated with {} records", salaryGlobalList.size());
        log.info("Global SalarySettings list populated with {} records", salarySettingsGlobalList.size());
    }

    // ── Salary mappers ──────────────────────────────────────────────────────

    public Salaries mapRequest_ToSalary(SalaryRequest r) {
        List<SalaryItem> items = r.getSalaryItems() == null ? new ArrayList<>()
                : r.getSalaryItems().stream().map(this::mapItemRequest_ToItem).collect(Collectors.toList());

        double totalAllowances = items.stream()
                .filter(i -> "ALLOWANCE".equalsIgnoreCase(i.getItemType()))
                .mapToDouble(i -> resolveAmount(i, r.getBasicSalary())).sum();

        double totalDeductions = items.stream()
                .filter(i -> "DEDUCTION".equalsIgnoreCase(i.getItemType()))
                .mapToDouble(i -> resolveAmount(i, r.getBasicSalary())).sum();

        double basic = r.getBasicSalary() != null ? r.getBasicSalary() : 0.0;
        double netPay = basic + totalAllowances - totalDeductions;

        return Salaries.builder()
                .staffId(r.getStaffId())
                .institutionCode(r.getInstitutionCode())
                .staffName(r.getStaffName())
                .designation(r.getDesignation())
                .academicYear(r.getAcademicYear())
                .payPeriod(r.getPayPeriod())
                .basicSalary(basic)
                .totalAllowances(totalAllowances)
                .totalDeductions(totalDeductions)
                .netSalary(netPay)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .paymentMethod(r.getPaymentMethod())
                .externalReference(r.getExternalReference())
                .salaryItems(items)
                .build();
    }

    public Salaries applyApproval(Salaries s, String processedBy) {
        s.setStatus("APPROVED");
        s.setProcessedBy(processedBy);
        return s;
    }

    public Salaries applyPayment(Salaries s, String processedBy, String reference) {
        s.setStatus("PAID");
        s.setProcessedBy(processedBy);
        s.setPaymentDate(java.time.LocalDate.now());
        if (reference != null) s.setExternalReference(reference);
        return s;
    }

    public SalaryResponse mapSalary_ToResponse(Salaries s) {
        List<SalaryItemResponse> items = s.getSalaryItems() == null ? new ArrayList<>()
                : s.getSalaryItems().stream().map(this::mapItem_ToResponse).collect(Collectors.toList());

        return SalaryResponse.builder()
                .salaryId(s.getSalaryId())
                .staffId(s.getStaffId())
                .institutionCode(s.getInstitutionCode())
                .staffName(s.getStaffName())
                .designation(s.getDesignation())
                .academicYear(s.getAcademicYear())
                .payPeriod(s.getPayPeriod())
                .basicSalary(s.getBasicSalary())
                .totalAllowances(s.getTotalAllowances())
                .totalDeductions(s.getTotalDeductions())
                .netSalary(s.getNetSalary())
                .status(s.getStatus())
                .paymentDate(s.getPaymentDate())
                .createdAt(s.getCreatedAt())
                .processedBy(s.getProcessedBy())
                .paymentMethod(s.getPaymentMethod())
                .externalReference(s.getExternalReference())
                .salaryItems(items)
                .build();
    }

    // ── SalarySettings mappers ───────────────────────────────────────────────

    public SalarySettings mapSettingsRequest_ToSettings(SalarySettingsRequest r) {
        List<SalaryItem> allowances = r.getDefaultAllowances() == null ? new ArrayList<>()
                : r.getDefaultAllowances().stream().map(this::mapItemRequest_ToItem).collect(Collectors.toList());

        return SalarySettings.builder()
                .institutionCode(r.getInstitutionCode())
                .ssnitEmployeeRate(r.getSsnitEmployeeRate() != null ? r.getSsnitEmployeeRate() : 5.5)
                .ssnitEmployerRate(r.getSsnitEmployerRate() != null ? r.getSsnitEmployerRate() : 13.0)
                .incomeTaxBands(r.getIncomeTaxBands())
                .defaultAllowances(allowances)
                .build();
    }

    public SalarySettingsResponse mapSettings_ToResponse(SalarySettings s) {
        List<SalaryItemResponse> allowances = s.getDefaultAllowances() == null ? new ArrayList<>()
                : s.getDefaultAllowances().stream().map(this::mapItem_ToResponse).collect(Collectors.toList());

        return SalarySettingsResponse.builder()
                .salarySettingsId(s.getSalarySettingsId())
                .institutionCode(s.getInstitutionCode())
                .ssnitEmployeeRate(s.getSsnitEmployeeRate())
                .ssnitEmployerRate(s.getSsnitEmployerRate())
                .incomeTaxBands(s.getIncomeTaxBands())
                .defaultAllowances(allowances)
                .build();
    }

    // ── Item helpers ─────────────────────────────────────────────────────────

    private SalaryItem mapItemRequest_ToItem(SalaryItemRequest r) {
        return SalaryItem.builder()
                .salaryItemId(r.getSalaryItemId())
                .itemName(r.getItemName())
                .itemType(r.getItemType())
                .amount(r.getAmount() != null ? r.getAmount() : 0.0)
                .isPercentage(r.getIsPercentage() != null && r.getIsPercentage())
                .percentageRate(r.getPercentageRate())
                .build();
    }

    private SalaryItemResponse mapItem_ToResponse(SalaryItem i) {
        return SalaryItemResponse.builder()
                .salaryItemId(i.getSalaryItemId())
                .itemName(i.getItemName())
                .itemType(i.getItemType())
                .amount(i.getAmount())
                .isPercentage(i.getIsPercentage())
                .percentageRate(i.getPercentageRate())
                .build();
    }

    /**
     * If a salary item is percentage-based, compute the actual GHS amount.
     * Otherwise return the fixed amount directly.
     */
    private double resolveAmount(SalaryItem item, Double basicSalary) {
        if (Boolean.TRUE.equals(item.getIsPercentage()) && item.getPercentageRate() != null) {
            double base = basicSalary != null ? basicSalary : 0.0;
            return (item.getPercentageRate() / 100.0) * base;
        }
        return item.getAmount() != null ? item.getAmount() : 0.0;
    }
}
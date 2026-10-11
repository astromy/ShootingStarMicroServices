package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.PayrollActionRequest;
import com.astromyllc.shootingstar.finance.dto.request.PayrollStaffRef;
import com.astromyllc.shootingstar.finance.dto.request.RunPayrollRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryItemRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryProfileRequest;
import com.astromyllc.shootingstar.finance.dto.response.PayrollActionResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalaryItemResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalaryProfileResponse;
import com.astromyllc.shootingstar.finance.model.Salaries;
import com.astromyllc.shootingstar.finance.model.SalaryItem;
import com.astromyllc.shootingstar.finance.model.SalaryProfile;
import com.astromyllc.shootingstar.finance.model.SalarySettings;
import com.astromyllc.shootingstar.finance.repositoy.SalariesRepository;
import com.astromyllc.shootingstar.finance.repositoy.SalaryProfileRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.PayrollServiceInterface;
import com.astromyllc.shootingstar.finance.utils.PayrollCalculator;
import com.astromyllc.shootingstar.finance.utils.SalaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollService implements PayrollServiceInterface {

    private static final Set<String> PAYMENT_METHODS = Set.of("BANK", "MOMO", "CASH");
    private static final Set<String> ITEM_TYPES = Set.of("ALLOWANCE", "DEDUCTION");

    private final SalaryProfileRepository salaryProfileRepository;
    private final SalariesRepository salariesRepository;
    private final SalaryUtil salaryUtil;
    private final LedgerPostingService ledgerPostingService;
    private final PayrollOvertimeSupport overtimeSupport;

    // ── Profiles ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<SalaryProfileResponse> getProfiles(String institutionCode) {
        String code = requireInstitution(institutionCode);
        return salaryProfileRepository.findByInstitutionCode(code).stream()
                .sorted(Comparator.comparing((SalaryProfile p) -> String.valueOf(p.getStaffName()), String.CASE_INSENSITIVE_ORDER))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SalaryProfileResponse saveProfile(SalaryProfileRequest r) {
        String code = requireInstitution(r.getInstitutionCode());
        String staffId = required(r.getStaffId(), "Choose a staff member from HR.");
        if (r.getBasicSalary() == null || r.getBasicSalary() <= 0) {
            throw new IllegalArgumentException("Enter a basic salary above zero.");
        }

        String method = r.getPaymentMethod() == null ? "" : r.getPaymentMethod().trim().toUpperCase(Locale.ROOT);
        if (!PAYMENT_METHODS.contains(method)) {
            throw new IllegalArgumentException("Choose how this staff member is paid: bank, mobile money or cash.");
        }
        if ("BANK".equals(method) && (isBlank(r.getBankName()) || isBlank(r.getAccountNumber()))) {
            throw new IllegalArgumentException("Enter the bank name and account number for bank payments.");
        }
        if ("MOMO".equals(method) && isBlank(r.getMomoNumber())) {
            throw new IllegalArgumentException("Enter the mobile money number.");
        }

        List<SalaryItem> items = validateItems(r.getItems());

        SalaryProfile profile = salaryProfileRepository.findByInstitutionCodeAndStaffIdIgnoreCase(code, staffId)
                .orElseGet(() -> SalaryProfile.builder().institutionCode(code).staffId(staffId).build());

        profile.setStaffName(r.getStaffName());
        profile.setDesignation(r.getDesignation());
        profile.setBasicSalary(round(r.getBasicSalary()));
        profile.setPaymentMethod(method);
        // Keep only the details that apply to the chosen method.
        profile.setBankName("BANK".equals(method) ? trim(r.getBankName()) : null);
        profile.setBankBranch("BANK".equals(method) ? trim(r.getBankBranch()) : null);
        profile.setAccountName("BANK".equals(method) ? trim(r.getAccountName()) : null);
        profile.setAccountNumber("BANK".equals(method) ? trim(r.getAccountNumber()) : null);
        profile.setMomoNumber("MOMO".equals(method) ? trim(r.getMomoNumber()) : null);
        profile.setActive(r.getActive() == null || r.getActive());
        profile.setUpdatedAt(LocalDateTime.now());
        profile.setUpdatedBy(r.getUpdatedBy());
        // Same list instance, so orphanRemoval deletes removed lines.
        profile.getItems().clear();
        profile.getItems().addAll(items);

        SalaryProfile saved = salaryProfileRepository.save(profile);

        // Ledger: an expense account per allowance, a liability account per deduction.
        for (SalaryItem item : items) {
            try {
                ledgerPostingService.ensureSalaryItemAccount(code, item.getItemName(), item.getItemType());
            } catch (Exception e) {
                log.error("Ledger: couldn't create the account for salary item '{}' at {}", item.getItemName(), code, e);
            }
        }
        log.info("Salary profile saved for staff {} at {} by {}", staffId, code, r.getUpdatedBy());
        return toResponse(saved);
    }

    // ── Payroll run ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public PayrollActionResponse runPayroll(RunPayrollRequest r) {
        String code = requireInstitution(r.getInstitutionCode());
        String payPeriod = required(r.getPayPeriod(), "Choose the month to pay.");
        String academicYear = required(r.getAcademicYear(), "Choose the academic year.");

        SalarySettings settings = SalaryUtil.salarySettingsGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Set up the SSNIT rates and income tax bands in Salary Settings before running payroll."));
        PayrollCalculator.parseBands(settings.getIncomeTaxBands()); // fail early with a clear message

        // HR is the list of who can be paid.
        Map<String, PayrollStaffRef> hrStaff = new HashMap<>();
        for (PayrollStaffRef s : r.getStaff() == null ? List.<PayrollStaffRef>of() : r.getStaff()) {
            if (s != null && !isBlank(s.getStaffId())) hrStaff.put(s.getStaffId().trim().toLowerCase(Locale.ROOT), s);
        }

        PayrollActionResponse result = new PayrollActionResponse();
        List<Salaries> created = new ArrayList<>();
        Map<String, SalaryProfile> profilesByStaff = new HashMap<>();

        // Approved overtime from HR for this pay month (nothing when overtime is off or none is approved).
        // If HR can't be reached the run stops here - paying without it would silently underpay.
        PayrollOvertimeSupport.Collected overtime = overtimeSupport.collect(code, payPeriod);
        Map<String, List<String>> overtimeUsed = new HashMap<>();

        for (SalaryProfile profile : salaryProfileRepository.findByInstitutionCode(code)) {
            String key = profile.getStaffId().toLowerCase(Locale.ROOT);
            profilesByStaff.put(key, profile);
            PayrollStaffRef hr = hrStaff.get(key);

            if (!Boolean.TRUE.equals(profile.getActive())) {
                skip(result, null, profile.getStaffId(), profile.getStaffName(), "Salary profile is inactive.");
                continue;
            }
            if (hr == null) {
                skip(result, null, profile.getStaffId(), profile.getStaffName(), "No longer listed in HR.");
                continue;
            }
            if (profile.getBasicSalary() == null || profile.getBasicSalary() <= 0) {
                skip(result, null, profile.getStaffId(), hr.getStaffName(), "No basic salary on the salary profile.");
                continue;
            }
            Optional<Salaries> existing = SalaryUtil.salaryGlobalList.stream()
                    .filter(s -> s.getInstitutionCode().equalsIgnoreCase(code)
                            && s.getStaffId().equalsIgnoreCase(profile.getStaffId())
                            && payPeriod.equalsIgnoreCase(s.getPayPeriod())
                            && academicYear.equalsIgnoreCase(s.getAcademicYear()))
                    .findFirst();
            if (existing.isPresent()) {
                skip(result, existing.get().getSalaryId(), profile.getStaffId(), hr.getStaffName(),
                        "Already has a " + existing.get().getStatus().toLowerCase(Locale.ROOT) + " run for " + payPeriod + ".");
                continue;
            }

            List<String> usedOvertime = new ArrayList<>();
            List<SalaryItem> payItems = overtimeSupport.withOvertime(profile.getBasicSalary(), profile.getItems(),
                    overtime, profile.getStaffId(), usedOvertime);
            if (!usedOvertime.isEmpty()) overtimeUsed.put(key, usedOvertime);
            PayrollCalculator.Result pay = PayrollCalculator.calculate(profile.getBasicSalary(), payItems, settings);
            created.add(Salaries.builder()
                    .staffId(profile.getStaffId())
                    .institutionCode(code)
                    .staffName(hr.getStaffName())
                    .designation(hr.getDesignation())
                    .academicYear(academicYear)
                    .payPeriod(payPeriod)
                    .basicSalary(pay.basic())
                    .totalAllowances(pay.totalAllowances())
                    .totalDeductions(pay.totalDeductions())
                    .netSalary(pay.netPay())
                    .grossPay(pay.grossPay())
                    .taxableIncome(pay.taxableIncome())
                    .employeeSsnit(pay.employeeSsnit())
                    .employerSsnit(pay.employerSsnit())
                    .incomeTax(pay.incomeTax())
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .createdBy(r.getCreatedBy())
                    .paymentMethod(profile.getPaymentMethod())
                    .bankName(profile.getBankName())
                    .bankBranch(profile.getBankBranch())
                    .accountName(profile.getAccountName())
                    .accountNumber(profile.getAccountNumber())
                    .momoNumber(profile.getMomoNumber())
                    .salaryItems(pay.items())
                    .build());
        }

        // Staff in HR who have no salary profile yet - listed so nobody is missed silently.
        hrStaff.forEach((key, s) -> {
            if (!profilesByStaff.containsKey(key)) {
                skip(result, null, s.getStaffId(), s.getStaffName(), "No salary profile yet.");
            }
        });

        List<Salaries> saved = salariesRepository.saveAll(created);
        // Lock the overtime to these runs. All-or-nothing in HR; if it fails the exception rolls this run back.
        overtimeSupport.lock(code, saved, overtimeUsed);
        SalaryUtil.salaryGlobalList.addAll(saved);
        saved.forEach(s -> result.getUpdated().add(salaryUtil.mapSalary_ToResponse(s)));

        log.info("Payroll run for {} {} ({}): {} created, {} skipped, by {}",
                code, payPeriod, academicYear, saved.size(), result.getSkipped().size(), r.getCreatedBy());
        return result;
    }

    // ── Approval and payment ────────────────────────────────────────────────

    @Override
    @Transactional
    public PayrollActionResponse approve(PayrollActionRequest r) {
        return transition(r, "PENDING", "Only pending runs can be approved.", salary ->
                salaryUtil.applyApproval(salary, r.getActor()));
    }

    @Override
    @Transactional
    public PayrollActionResponse markPaid(PayrollActionRequest r) {
        String reference = isBlank(r.getExternalReference()) ? null : r.getExternalReference().trim();
        PayrollActionResponse result = transition(r, "APPROVED", "Only approved runs can be marked as paid.", salary ->
                salaryUtil.applyPayment(salary, r.getActor(), reference));

        // Ledger: salary expense, amounts withheld (SSNIT, PAYE, ...) and net pay out.
        for (var paid : result.getUpdated()) {
            SalaryUtil.salaryGlobalList.stream()
                    .filter(s -> s.getSalaryId().equals(paid.getSalaryId()))
                    .findFirst()
                    .ifPresent(s -> {
                        try {
                            ledgerPostingService.postSalaryPayment(s);
                        } catch (Exception e) {
                            log.error("Ledger: couldn't post salary payment {} for {}", s.getSalaryId(), s.getStaffName(), e);
                        }
                    });
        }
        return result;
    }

    @Override
    @Transactional
    public PayrollActionResponse deletePending(PayrollActionRequest r) {
        String code = requireInstitution(r.getInstitutionCode());
        PayrollActionResponse result = new PayrollActionResponse();
        List<Salaries> toDelete = new ArrayList<>();

        for (Long id : requireIds(r)) {
            Optional<Salaries> found = findForInstitution(id, code);
            if (found.isEmpty()) {
                skip(result, id, null, null, "Run not found.");
            } else if (!"PENDING".equalsIgnoreCase(found.get().getStatus())) {
                skip(result, id, found.get().getStaffId(), found.get().getStaffName(), "Only pending runs can be removed.");
            } else {
                toDelete.add(found.get());
                result.getUpdated().add(salaryUtil.mapSalary_ToResponse(found.get()));
            }
        }
        // Overtime the removed runs took goes back to HR so the next run can pay it.
        overtimeSupport.release(code, toDelete);
        salariesRepository.deleteAll(toDelete);
        SalaryUtil.salaryGlobalList.removeAll(toDelete);
        log.info("{} pending salary runs removed at {} by {}", toDelete.size(), code, r.getActor());
        return result;
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private interface SalaryChange {
        void apply(Salaries salary);
    }

    private PayrollActionResponse transition(PayrollActionRequest r, String requiredStatus, String wrongStatusReason,
                                             SalaryChange change) {
        String code = requireInstitution(r.getInstitutionCode());
        required(r.getActor(), "Your login couldn't be identified. Sign in again.");
        PayrollActionResponse result = new PayrollActionResponse();
        List<Salaries> changed = new ArrayList<>();

        for (Long id : requireIds(r)) {
            Optional<Salaries> found = findForInstitution(id, code);
            if (found.isEmpty()) {
                skip(result, id, null, null, "Run not found.");
            } else if (!requiredStatus.equalsIgnoreCase(found.get().getStatus())) {
                skip(result, id, found.get().getStaffId(), found.get().getStaffName(), wrongStatusReason);
            } else {
                change.apply(found.get());
                changed.add(found.get());
            }
        }
        salariesRepository.saveAll(changed);
        changed.forEach(s -> result.getUpdated().add(salaryUtil.mapSalary_ToResponse(s)));
        return result;
    }

    /** Looks a run up in the in-memory list (kept current by this service), for this school only. */
    private Optional<Salaries> findForInstitution(Long id, String institutionCode) {
        return SalaryUtil.salaryGlobalList.stream()
                .filter(s -> s.getSalaryId().equals(id) && s.getInstitutionCode().equalsIgnoreCase(institutionCode))
                .findFirst();
    }

    private static List<Long> requireIds(PayrollActionRequest r) {
        if (r.getSalaryIds() == null || r.getSalaryIds().isEmpty()) {
            throw new IllegalArgumentException("Select at least one salary run.");
        }
        return r.getSalaryIds();
    }

    private List<SalaryItem> validateItems(List<SalaryItemRequest> requests) {
        List<SalaryItem> items = new ArrayList<>();
        for (SalaryItemRequest i : requests == null ? List.<SalaryItemRequest>of() : requests) {
            if (i == null) continue;
            String name = required(i.getItemName(), "Every allowance or deduction needs a name.");
            String type = i.getItemType() == null ? "" : i.getItemType().trim().toUpperCase(Locale.ROOT);
            if (!ITEM_TYPES.contains(type)) {
                throw new IllegalArgumentException("\"" + name + "\" must be an allowance or a deduction.");
            }
            boolean pct = Boolean.TRUE.equals(i.getIsPercentage());
            double rate = i.getPercentageRate() == null ? 0 : i.getPercentageRate();
            double amount = i.getAmount() == null ? 0 : i.getAmount();
            if (pct && (rate <= 0 || rate > 100)) {
                throw new IllegalArgumentException("\"" + name + "\" needs a percentage between 0 and 100.");
            }
            if (!pct && amount <= 0) {
                throw new IllegalArgumentException("\"" + name + "\" needs an amount above zero.");
            }
            items.add(SalaryItem.builder()
                    .itemName(name)
                    .itemType(type)
                    .isPercentage(pct)
                    .percentageRate(pct ? rate : null)
                    .amount(pct ? 0.0 : round(amount))
                    .build());
        }
        return items;
    }

    private SalaryProfileResponse toResponse(SalaryProfile p) {
        return SalaryProfileResponse.builder()
                .salaryProfileId(p.getSalaryProfileId())
                .staffId(p.getStaffId())
                .staffName(p.getStaffName())
                .designation(p.getDesignation())
                .basicSalary(p.getBasicSalary())
                .paymentMethod(p.getPaymentMethod())
                .bankName(p.getBankName())
                .bankBranch(p.getBankBranch())
                .accountName(p.getAccountName())
                .accountNumber(p.getAccountNumber())
                .momoNumber(p.getMomoNumber())
                .active(p.getActive())
                .updatedAt(p.getUpdatedAt())
                .updatedBy(p.getUpdatedBy())
                .items(p.getItems().stream().map(i -> SalaryItemResponse.builder()
                        .salaryItemId(i.getSalaryItemId())
                        .itemName(i.getItemName())
                        .itemType(i.getItemType())
                        .amount(i.getAmount())
                        .isPercentage(i.getIsPercentage())
                        .percentageRate(i.getPercentageRate())
                        .build()).toList())
                .build();
    }

    private static void skip(PayrollActionResponse result, Long salaryId, String staffId, String staffName, String reason) {
        result.getSkipped().add(new PayrollActionResponse.Skipped(salaryId, staffId, staffName, reason));
    }

    private static String requireInstitution(String code) {
        if (isBlank(code)) throw new IllegalArgumentException("Your account isn't linked to a school.");
        return code.trim();
    }

    private static String required(String value, String message) {
        if (isBlank(value)) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static String trim(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

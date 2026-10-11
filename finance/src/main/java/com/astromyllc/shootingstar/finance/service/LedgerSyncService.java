package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.model.Bill;
import com.astromyllc.shootingstar.finance.model.Bill_Payment;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.model.Salaries;
import com.astromyllc.shootingstar.finance.model.SalaryItem;
import com.astromyllc.shootingstar.finance.model.SalaryProfile;
import com.astromyllc.shootingstar.finance.repositoy.SalaryProfileRepository;
import com.astromyllc.shootingstar.finance.utils.BillUtil;
import com.astromyllc.shootingstar.finance.utils.Bill_PaymentUtil;
import com.astromyllc.shootingstar.finance.utils.BillingsUtil;
import com.astromyllc.shootingstar.finance.utils.SalaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Brings a school's ledger up to date with everything recorded before
 * automatic posting existed (or anything a posting failure missed):
 * accounts for every bill item and salary item, then journals for every
 * billing, fee payment and paid salary run.
 *
 * Safe to run any number of times - journals already in the ledger are skipped.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerSyncService {

    private final LedgerPostingService ledgerPostingService;
    private final SalaryProfileRepository salaryProfileRepository;

    public Map<String, Object> syncInstitution(String institutionCode) {
        if (institutionCode == null || institutionCode.isBlank()) {
            throw new IllegalArgumentException("Your account isn't linked to a school.");
        }
        String code = institutionCode.trim();
        List<String> failures = new ArrayList<>();

        for (Bill bill : BillUtil.billGlobalList) {
            if (!code.equalsIgnoreCase(bill.getInstitutionCode())) continue;
            run(failures, "bill item " + bill.getBill_Name(),
                    () -> ledgerPostingService.ensureBillItemAccount(code, bill.getBill_Name()));
        }
        for (SalaryProfile profile : salaryProfileRepository.findByInstitutionCode(code)) {
            for (SalaryItem item : profile.getItems()) {
                run(failures, "salary item " + item.getItemName(),
                        () -> ledgerPostingService.ensureSalaryItemAccount(code, item.getItemName(), item.getItemType()));
            }
        }

        int billings = 0, payments = 0, salaries = 0;
        for (Billings b : new ArrayList<>(BillingsUtil.billingGlobalList)) {
            if (!code.equalsIgnoreCase(b.getInstitutionCode())) continue;
            if (post(failures, "billing " + b.getBillingId(), () -> ledgerPostingService.postBilling(b))) billings++;
        }
        for (Bill_Payment p : new ArrayList<>(Bill_PaymentUtil.billPaymentGlobalList)) {
            if (!code.equalsIgnoreCase(p.getInstitutionCode())) continue;
            if (post(failures, "fee payment " + p.getBillPaymentId(), () -> ledgerPostingService.postFeePayment(p))) payments++;
        }
        for (Salaries s : new ArrayList<>(SalaryUtil.salaryGlobalList)) {
            if (!code.equalsIgnoreCase(s.getInstitutionCode()) || !"PAID".equalsIgnoreCase(s.getStatus())) continue;
            if (post(failures, "salary run " + s.getSalaryId(), () -> ledgerPostingService.postSalaryPayment(s))) salaries++;
        }

        log.info("Ledger sync for {}: {} billings, {} fee payments, {} salary runs posted; {} failures",
                code, billings, payments, salaries, failures.size());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("billingsPosted", billings);
        result.put("feePaymentsPosted", payments);
        result.put("salaryRunsPosted", salaries);
        result.put("failures", failures);
        return result;
    }

    private interface Posting {
        boolean post();
    }

    private static boolean post(List<String> failures, String what, Posting posting) {
        try {
            return posting.post();
        } catch (Exception e) {
            log.error("Ledger sync: couldn't post {}", what, e);
            failures.add(what + ": " + e.getMessage());
            return false;
        }
    }

    private static void run(List<String> failures, String what, Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            log.error("Ledger sync: couldn't create the account for {}", what, e);
            failures.add(what + ": " + e.getMessage());
        }
    }
}

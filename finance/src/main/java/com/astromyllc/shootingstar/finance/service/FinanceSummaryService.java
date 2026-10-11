package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.response.FinanceSummaryResponse;
import com.astromyllc.shootingstar.finance.dto.response.LabelAmount;
import com.astromyllc.shootingstar.finance.dto.response.LabelCount;
import com.astromyllc.shootingstar.finance.model.Bill;
import com.astromyllc.shootingstar.finance.model.Bill_Payment;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.utils.BillUtil;
import com.astromyllc.shootingstar.finance.utils.BillingsUtil;
import com.astromyllc.shootingstar.finance.utils.Bill_PaymentUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Fee totals for the dashboards, worked out in one pass over the in-memory lists
 * ({@link BillUtil#billGlobalList}, {@link BillingsUtil#billingGlobalList},
 * {@link Bill_PaymentUtil#billPaymentGlobalList}). Nothing is cached, so a new payment shows
 * up on the next request.
 */
@Service
public class FinanceSummaryService {

    public FinanceSummaryResponse summary(String institutionCode) {
        return summarize(copy(BillUtil.billGlobalList), copy(BillingsUtil.billingGlobalList),
                copy(Bill_PaymentUtil.billPaymentGlobalList), institutionCode);
    }

    private static <T> List<T> copy(List<T> live) {
        // a copy, because payments and bills are added while other requests read
        return live == null ? List.of() : new ArrayList<>(live);
    }

    static FinanceSummaryResponse summarize(List<Bill> bills, List<Billings> billings, List<Bill_Payment> payments, String institutionCode) {
        long billCount = 0, paymentCount = 0, billingsOwing = 0, billingsPaid = 0;
        double collected = 0, billed = 0, outstanding = 0;
        Set<String> owing = new HashSet<>();
        Set<String> billedStudents = new HashSet<>();
        Map<String, Long> methods = new HashMap<>();
        Map<String, Double> byClass = new HashMap<>();

        if (institutionCode == null || institutionCode.isBlank()) {
            return new FinanceSummaryResponse(0, 0, 0, 0, 0, 0, 0, 0, 0, List.of(), List.of());
        }

        for (Bill b : bills) {
            if (b != null && same(b.getInstitutionCode(), institutionCode)) billCount++;
        }

        // The class a student is billed in (the latest bill wins), so a payment can be put against a class
        Map<String, String> classOf = new HashMap<>();
        Map<String, LocalDateTime> classDate = new HashMap<>();
        for (Billings b : billings) {
            if (b == null || !same(b.getInstitutionCode(), institutionCode)) continue;
            billed += nz(b.getBillamnt());
            if (b.getStudentId() != null) billedStudents.add(key(b.getStudentId()));
            double balance = nz(b.getBillamntbal());
            if (balance > 0) {
                billingsOwing++;
                outstanding += balance;
                if (b.getStudentId() != null) owing.add(key(b.getStudentId()));
            } else {
                billingsPaid++;
            }
            if (b.getStudentId() != null && b.getStudentClass() != null && !b.getStudentClass().isBlank()) {
                String student = key(b.getStudentId());
                LocalDateTime when = b.getBillingDate();
                LocalDateTime seen = classDate.get(student);
                if (seen == null || (when != null && when.isAfter(seen))) {
                    classOf.put(student, b.getStudentClass().trim());
                    classDate.put(student, when);
                }
            }
        }

        for (Bill_Payment p : payments) {
            if (p == null || !same(p.getInstitutionCode(), institutionCode)) continue;
            paymentCount++;
            double amount = nz(p.getPaymentAmount());
            collected += amount;
            String method = p.getPaymentMethod() == null || p.getPaymentMethod().isBlank() ? "Cash" : p.getPaymentMethod().trim();
            methods.merge(method, 1L, Long::sum);
            String cls = p.getStudentId() == null ? null : classOf.get(key(p.getStudentId()));
            byClass.merge(cls == null ? "Unknown" : cls, amount, Double::sum);
        }

        List<LabelAmount> classes = new ArrayList<>();
        byClass.forEach((label, amount) -> classes.add(new LabelAmount(label, round2(amount))));
        classes.sort((a, b) -> {
            int c = Double.compare(b.amount(), a.amount());
            return c != 0 ? c : a.label().compareTo(b.label());
        });
        List<LabelCount> methodCounts = new ArrayList<>();
        methods.forEach((label, n) -> methodCounts.add(new LabelCount(label, n)));
        methodCounts.sort((a, b) -> {
            int c = Long.compare(b.count(), a.count());
            return c != 0 ? c : a.label().compareTo(b.label());
        });

        return new FinanceSummaryResponse(billCount, paymentCount, round2(collected), round2(billed), round2(outstanding),
                billedStudents.size(), owing.size(), billingsOwing, billingsPaid, classes, methodCounts);
    }

    private static double nz(Double value) {
        return value == null ? 0 : value;
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static String key(String studentId) {
        return studentId.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean same(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }
}

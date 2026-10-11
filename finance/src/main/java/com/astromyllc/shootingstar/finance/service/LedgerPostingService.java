package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.model.Bill_Payment;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.model.LedgerBooks;
import com.astromyllc.shootingstar.finance.model.LedgerRecords;
import com.astromyllc.shootingstar.finance.model.Salaries;
import com.astromyllc.shootingstar.finance.model.SalaryItem;
import com.astromyllc.shootingstar.finance.repositoy.LedgerBooksRepository;
import com.astromyllc.shootingstar.finance.repositoy.LedgerRecordsRepository;
import com.astromyllc.shootingstar.finance.utils.PayrollCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Keeps the ledger books in step with billing, fee collection and payroll,
 * using double entry so every journal balances (total debits = total credits)
 * and the trial balance always agrees.
 *
 * Accounts (created automatically the first time they're needed):
 *   1001 Cash at Hand            ASSET      fees received / salaries paid in cash
 *   1002 Bank Account            ASSET      bank, cheque and online (Paystack) payments
 *   1003 Mobile Money            ASSET      MoMo payments
 *   1100 Student Fees Receivable ASSET      what students owe
 *   2001 SSNIT Payable           LIABILITY  employee + employer SSNIT withheld, owed to SSNIT
 *   2002 PAYE Payable            LIABILITY  income tax withheld, owed to GRA
 *   21xx <deduction> Payable     LIABILITY  one per other salary deduction (welfare, loans, ...)
 *   41xx Fees – <bill item>      INCOME     one per bill item
 *   5001 Salary Expense          EXPENSE    basic salaries
 *   5002 Employer SSNIT Contribution EXPENSE
 *   51xx Allowance – <name>      EXPENSE    one per salary allowance
 *
 * Journals:
 *   Student billed   Dr Student Fees Receivable        Cr Fees – <item>
 *   Fee collected    Dr Cash / Bank / Mobile Money      Cr Student Fees Receivable
 *   Salary paid      Dr Salary Expense, allowances, Employer SSNIT
 *                    Cr SSNIT Payable, PAYE Payable, other deductions, Cash / Bank / MoMo (net pay)
 *
 * Each journal is posted once: its lines share a sourceReference (BL-<billingId>,
 * BP-<billPaymentId>, SAL-<salaryId>), and a reference already in the ledger is
 * skipped - so posting again (e.g. the "sync" for older records) is safe.
 *
 * Every public method runs in its own transaction (REQUIRES_NEW), and callers
 * catch and log failures: a ledger problem is logged loudly but never blocks
 * collecting a fee or paying a salary. Anything missed can be posted later with
 * syncInstitution (Ledger Books -> Sync), which is idempotent.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerPostingService {

    public static final String SOURCE_BILLING = "BILLING";
    public static final String SOURCE_FEE = "STUDENT_PAYMENT";
    public static final String SOURCE_SALARY = "SALARY";

    private record Account(String name, String code, String type, String description) {
    }

    private record Line(LedgerBooks book, String entryType, double amount, String description) {
    }

    private static final Account CASH = new Account("Cash at Hand", "1001", "ASSET", "Cash received and paid out");
    private static final Account BANK = new Account("Bank Account", "1002", "ASSET", "Bank, cheque and online payments");
    private static final Account MOMO = new Account("Mobile Money", "1003", "ASSET", "Mobile money payments");
    private static final Account RECEIVABLE = new Account("Student Fees Receivable", "1100", "ASSET", "Fees billed to students and not yet paid");
    private static final Account SSNIT_PAYABLE = new Account("SSNIT Payable", "2001", "LIABILITY", "Employee and employer SSNIT contributions owed to SSNIT");
    private static final Account PAYE_PAYABLE = new Account("PAYE Payable", "2002", "LIABILITY", "Income tax withheld from salaries, owed to GRA");
    private static final Account SALARY_EXPENSE = new Account("Salary Expense", "5001", "EXPENSE", "Basic salaries");
    private static final Account EMPLOYER_SSNIT = new Account("Employer SSNIT Contribution", "5002", "EXPENSE", "The school's SSNIT contribution for staff");

    private final LedgerBooksRepository ledgerBooksRepository;
    private final LedgerRecordsRepository ledgerRecordsRepository;

    // ── Accounts for bill and salary items ──────────────────────────────────

    /** Income account for a bill item ("Fees – Tuition"). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureBillItemAccount(String institutionCode, String billName) {
        feesAccount(institutionCode, billName);
    }

    /** Expense account for an allowance, or liability account for a deduction. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureSalaryItemAccount(String institutionCode, String itemName, String itemType) {
        if ("DEDUCTION".equalsIgnoreCase(itemType)) {
            deductionAccount(institutionCode, itemName);
        } else {
            allowanceAccount(institutionCode, itemName);
        }
    }

    // ── Journals ────────────────────────────────────────────────────────────

    /** Student billed for one bill item. Returns false if already posted. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean postBilling(Billings b) {
        double amount = value(b.getBillamnt());
        String inst = b.getInstitutionCode();
        String description = b.getBillname() + " – " + b.getStudentId()
                + (b.getTerm() != null ? " (" + b.getTerm() + ", " + b.getAcademicYear() + ")" : "");
        return postJournal(inst, SOURCE_BILLING, "BL-" + b.getBillingId(), null, null,
                b.getBillingDate(), List.of(
                        new Line(account(inst, RECEIVABLE), "DEBIT", amount, description),
                        new Line(feesAccount(inst, b.getBillname()), "CREDIT", amount, description)));
    }

    /** Fee collected from a student. Returns false if already posted. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean postFeePayment(Bill_Payment p) {
        double amount = value(p.getPaymentAmount());
        String inst = p.getInstitutionCode();
        String description = "Fees received – " + p.getStudentId()
                + (p.getRecieptNum() != null ? " (receipt " + p.getRecieptNum() + ")" : "");
        return postJournal(inst, SOURCE_FEE, "BP-" + p.getBillPaymentId(), p.getExternalReference(), p.getPaidBy(),
                p.getPaymentDate(), List.of(
                        new Line(account(inst, cashAccountFor(p.getPaymentMethod())), "DEBIT", amount, description),
                        new Line(account(inst, RECEIVABLE), "CREDIT", amount, description)));
    }

    /** Salary run paid. Returns false if already posted. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean postSalaryPayment(Salaries s) {
        String inst = s.getInstitutionCode();
        String who = (s.getStaffName() != null ? s.getStaffName() : s.getStaffId()) + " – " + s.getPayPeriod();
        double basic = value(s.getBasicSalary());
        double employerSsnit = value(s.getEmployerSsnit());

        List<Line> lines = new ArrayList<>();
        lines.add(new Line(account(inst, SALARY_EXPENSE), "DEBIT", basic, "Basic salary – " + who));

        double debits = basic;
        double withheld = 0;
        for (SalaryItem item : s.getSalaryItems() == null ? List.<SalaryItem>of() : s.getSalaryItems()) {
            double amount = itemAmount(item, basic);
            if (amount <= 0) continue;
            String description = item.getItemName() + " – " + who;
            if ("DEDUCTION".equalsIgnoreCase(item.getItemType())) {
                LedgerBooks book = PayrollCalculator.CODE_SSNIT_EMPLOYEE.equals(item.getItemCode()) ? account(inst, SSNIT_PAYABLE)
                        : PayrollCalculator.CODE_PAYE.equals(item.getItemCode()) ? account(inst, PAYE_PAYABLE)
                        : deductionAccount(inst, item.getItemName());
                lines.add(new Line(book, "CREDIT", amount, description));
                withheld += amount;
            } else {
                lines.add(new Line(allowanceAccount(inst, item.getItemName()), "DEBIT", amount, description));
                debits += amount;
            }
        }
        if (employerSsnit > 0) {
            lines.add(new Line(account(inst, EMPLOYER_SSNIT), "DEBIT", employerSsnit, "Employer SSNIT – " + who));
            lines.add(new Line(account(inst, SSNIT_PAYABLE), "CREDIT", employerSsnit, "Employer SSNIT – " + who));
        }

        // Net pay is what's left after deductions, so the journal always
        // balances. It should match the run's net salary; a difference means
        // the run's own totals are inconsistent (e.g. very old runs).
        double net = round(debits - withheld);
        if (s.getNetSalary() != null && Math.abs(net - s.getNetSalary()) > 0.01) {
            log.warn("Salary run {}: net pay from its lines ({}) differs from its net salary ({}) - posting {}",
                    s.getSalaryId(), net, s.getNetSalary(), net);
        }
        lines.add(new Line(account(inst, cashAccountFor(s.getPaymentMethod())), "CREDIT", net, "Net salary paid – " + who));

        return postJournal(inst, SOURCE_SALARY, "SAL-" + s.getSalaryId(), s.getExternalReference(), s.getProcessedBy(),
                s.getPaidAt() != null ? s.getPaidAt() : LocalDateTime.now(), lines);
    }

    // ── Posting ─────────────────────────────────────────────────────────────

    private boolean postJournal(String institutionCode, String sourceType, String reference, String externalRef,
                                String postedBy, LocalDateTime entryDate, List<Line> lines) {
        if (ledgerRecordsRepository.existsByInstitutionCodeAndSourceTypeAndSourceReference(institutionCode, sourceType, reference)) {
            return false;
        }
        double dr = 0, cr = 0;
        for (Line l : lines) {
            if ("DEBIT".equals(l.entryType())) dr += l.amount();
            else cr += l.amount();
        }
        if (Math.abs(dr - cr) > 0.01) {
            throw new IllegalStateException("Journal " + reference + " doesn't balance: debits " + dr + ", credits " + cr);
        }

        LocalDateTime date = entryDate != null ? entryDate : LocalDateTime.now();
        for (Line l : lines) {
            if (l.amount() == 0) continue;
            LedgerBooks book = l.book();
            boolean creditNormal = "INCOME".equals(book.getAccountType())
                    || "LIABILITY".equals(book.getAccountType())
                    || "EQUITY".equals(book.getAccountType());
            boolean increases = creditNormal ? "CREDIT".equals(l.entryType()) : "DEBIT".equals(l.entryType());
            double delta = increases ? l.amount() : -l.amount();
            book.setCurrentBalance(round(value(book.getCurrentBalance()) + delta));
            ledgerBooksRepository.save(book);

            ledgerRecordsRepository.save(LedgerRecords.builder()
                    .institutionCode(institutionCode)
                    .ledgerBookId(book.getLedgerBookId())
                    .entryType(l.entryType())
                    .amount(round(l.amount()))
                    .description(l.description())
                    .sourceType(sourceType)
                    .sourceReference(reference)
                    .externalReference(externalRef)
                    .postedBy(postedBy)
                    .entryDate(date)
                    .postedAt(LocalDateTime.now())
                    .build());
        }
        log.info("Ledger journal {} posted for {} ({} lines, {})", reference, institutionCode, lines.size(), round(dr));
        return true;
    }

    // ── Accounts ────────────────────────────────────────────────────────────

    private LedgerBooks account(String institutionCode, Account a) {
        return ledgerBooksRepository.findByInstitutionCodeAndAccountName(institutionCode, a.name())
                .orElseGet(() -> create(institutionCode, a.name(), a.code(), a.type(), a.description()));
    }

    private LedgerBooks feesAccount(String institutionCode, String billName) {
        return named(institutionCode, "Fees – " + clean(billName), "41", "INCOME", "Fees billed for " + clean(billName));
    }

    private LedgerBooks allowanceAccount(String institutionCode, String name) {
        return named(institutionCode, "Allowance – " + clean(name), "51", "EXPENSE", "Staff " + clean(name) + " allowance");
    }

    private LedgerBooks deductionAccount(String institutionCode, String name) {
        return named(institutionCode, clean(name) + " Payable", "21", "LIABILITY", clean(name) + " withheld from salaries");
    }

    /** Finds an item account by name, or creates it with the next code in its range (e.g. 4101, 4102, ...). */
    private LedgerBooks named(String institutionCode, String name, String codePrefix, String type, String description) {
        return ledgerBooksRepository.findByInstitutionCodeAndAccountName(institutionCode, name)
                .orElseGet(() -> create(institutionCode, name, nextCode(institutionCode, codePrefix), type, description));
    }

    private LedgerBooks create(String institutionCode, String name, String code, String type, String description) {
        log.info("Creating ledger account '{}' ({} {}) for {}", name, code, type, institutionCode);
        return ledgerBooksRepository.save(LedgerBooks.builder()
                .institutionCode(institutionCode)
                .accountName(name)
                .accountCode(code)
                .accountType(type)
                .description(description)
                .currentBalance(0.0)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private String nextCode(String institutionCode, String prefix) {
        int max = ledgerBooksRepository.findByInstitutionCode(institutionCode).stream()
                .map(LedgerBooks::getAccountCode)
                .filter(c -> c != null && c.length() == 4 && c.startsWith(prefix) && c.chars().allMatch(Character::isDigit))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(Integer.parseInt(prefix + "00"));
        return String.valueOf(max + 1);
    }

    private static Account cashAccountFor(String paymentMethod) {
        String m = paymentMethod == null ? "" : paymentMethod.trim().toUpperCase(Locale.ROOT);
        if (m.isEmpty() || m.equals("CASH")) return CASH;
        if (m.contains("MOMO") || m.contains("MOBILE")) return MOMO;
        return BANK; // BANK, CHEQUE, ONLINE (Paystack settles to the bank), card
    }

    /** Old runs stored % lines with amount 0; work their amount out from basic. */
    private static double itemAmount(SalaryItem item, double basic) {
        double amount = value(item.getAmount());
        if (amount == 0 && Boolean.TRUE.equals(item.getIsPercentage())) {
            amount = round(basic * value(item.getPercentageRate()) / 100.0);
        }
        return amount;
    }

    private static String clean(String name) {
        return name == null || name.isBlank() ? "Other" : name.trim();
    }

    private static double value(Double d) {
        return d == null ? 0 : d;
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

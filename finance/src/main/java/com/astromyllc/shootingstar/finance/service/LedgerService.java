package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.LedgerBooksRequest;
import com.astromyllc.shootingstar.finance.dto.request.LedgerFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.LedgerRecordsFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.StoreSaleLedgerRequest;
import com.astromyllc.shootingstar.finance.dto.response.LedgerBooksResponse;
import com.astromyllc.shootingstar.finance.dto.response.LedgerRecordsResponse;
import com.astromyllc.shootingstar.finance.model.LedgerBooks;
import com.astromyllc.shootingstar.finance.model.LedgerRecords;
import com.astromyllc.shootingstar.finance.repositoy.LedgerBooksRepository;
import com.astromyllc.shootingstar.finance.repositoy.LedgerRecordsRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.LedgerServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerService implements LedgerServiceInterface {

    private static final String STORES_INCOME_ACCOUNT = "Stores & Prospectus Income";
    private static final String STUDENT_FEES_ACCOUNT = "Student Fees Income";
    private static final String SALARY_EXPENSE_ACCOUNT = "Salary Expense";
    private final LedgerBooksRepository ledgerBooksRepository;
    private final LedgerRecordsRepository ledgerRecordsRepository;

    // ── Chart of Accounts ────────────────────────────────────────────────────

    @Override
    @Transactional
    public LedgerBooksResponse createOrUpdateAccount(LedgerBooksRequest req) {
        LedgerBooks book;
        if (req.getLedgerBookId() != null) {
            book = ledgerBooksRepository.findById(req.getLedgerBookId())
                    .orElseThrow(() -> new RuntimeException("Ledger account not found: " + req.getLedgerBookId()));
            book.setAccountName(req.getAccountName());
            book.setAccountCode(req.getAccountCode());
            book.setAccountType(req.getAccountType());
            book.setDescription(req.getDescription());
        } else {
            book = LedgerBooks.builder()
                    .institutionCode(req.getInstitutionCode())
                    .accountName(req.getAccountName())
                    .accountCode(req.getAccountCode())
                    .accountType(req.getAccountType())
                    .description(req.getDescription())
                    .currentBalance(0.0)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toBookResponse(ledgerBooksRepository.save(book));
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    public List<LedgerBooksResponse> getAccountsByInstitution(LedgerFetchRequest req) {
        List<LedgerBooks> books;
        if (req.getAccountType() != null && !req.getAccountType().isBlank()) {
            books = ledgerBooksRepository.findByInstitutionCodeAndAccountTypeAndActiveTrue(
                    req.getInstitutionCode(), req.getAccountType());
        } else {
            books = ledgerBooksRepository.findByInstitutionCodeAndActiveTrue(req.getInstitutionCode());
        }
        return books.stream().map(this::toBookResponse).toList();
    }

    @Override
    public List<LedgerRecordsResponse> getRecordsByAccount(LedgerRecordsFetchRequest req) {
        return ledgerRecordsRepository
                .findByInstitutionCodeAndLedgerBookIdOrderByEntryDateDesc(
                        req.getInstitutionCode(), req.getLedgerBookId())
                .stream().map(this::toRecordResponse).toList();
    }

    // ── Store Sale (called by Stores-Inventory microservice) ──────────────────

    @Override
    @Transactional
    public LedgerRecordsResponse postStoreSale(StoreSaleLedgerRequest req) {
        if (ledgerRecordsRepository.findBySourceReferenceAndSourceType(req.getOrderRef(), "STORE_SALE").isPresent()) {
            log.warn("Duplicate store-sale ledger post ignored for orderRef={}", req.getOrderRef());
            return toRecordResponse(ledgerRecordsRepository
                    .findBySourceReferenceAndSourceType(req.getOrderRef(), "STORE_SALE").get());
        }

        LedgerBooks book = resolveAccount(req.getInstitutionCode(), STORES_INCOME_ACCOUNT, "4002", "INCOME",
                "Revenue from store, uniform and prospectus sales");

        LocalDateTime entryDate = req.getTransactionDate() != null
                ? LocalDateTime.parse(req.getTransactionDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : LocalDateTime.now();

        LedgerRecords record = postEntry(book, req.getInstitutionCode(), "CREDIT", req.getAmount(),
                "STORE_SALE", req.getOrderRef(), req.getPaymentReference(),
                req.getDescription(), req.getPostedBy(), entryDate);

        log.info("Ledger CREDIT posted: {} {} {} ref={}",
                STORES_INCOME_ACCOUNT, req.getInstitutionCode(), req.getAmount(), req.getOrderRef());
        return toRecordResponse(record);
    }

    // ── Student Payment ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public LedgerRecordsResponse postStudentPayment(String institutionCode, Long billPaymentId,
                                                    String studentId, Double amount,
                                                    String paymentMethod, String externalRef,
                                                    String description, String postedBy) {
        String refId = "BP-" + billPaymentId;
        if (ledgerRecordsRepository.findBySourceReferenceAndSourceType(refId, "STUDENT_PAYMENT").isPresent()) {
            log.warn("Duplicate student-payment ledger post ignored for billPaymentId={}", billPaymentId);
            return toRecordResponse(ledgerRecordsRepository
                    .findBySourceReferenceAndSourceType(refId, "STUDENT_PAYMENT").get());
        }

        LedgerBooks book = resolveAccount(institutionCode, STUDENT_FEES_ACCOUNT, "4001", "INCOME",
                "Student fee receipts");

        LedgerRecords record = postEntry(book, institutionCode, "CREDIT", amount,
                "STUDENT_PAYMENT", refId, externalRef,
                description != null ? description : "Student payment – " + studentId,
                postedBy, LocalDateTime.now());

        return toRecordResponse(record);
    }

    // ── Salary Expense ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LedgerRecordsResponse postSalaryExpense(String institutionCode, Long salaryId,
                                                   String staffName, Double netSalary,
                                                   String paymentMethod, String externalRef,
                                                   String postedBy) {
        String refId = "SAL-" + salaryId;
        if (ledgerRecordsRepository.findBySourceReferenceAndSourceType(refId, "SALARY").isPresent()) {
            log.warn("Duplicate salary ledger post ignored for salaryId={}", salaryId);
            return toRecordResponse(ledgerRecordsRepository
                    .findBySourceReferenceAndSourceType(refId, "SALARY").get());
        }

        LedgerBooks book = resolveAccount(institutionCode, SALARY_EXPENSE_ACCOUNT, "5001", "EXPENSE",
                "Staff salary payments");

        LedgerRecords record = postEntry(book, institutionCode, "DEBIT", netSalary,
                "SALARY", refId, externalRef,
                "Salary payment – " + staffName, postedBy, LocalDateTime.now());

        return toRecordResponse(record);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Gets or creates a LedgerBooks account for an institution.
     * Auto-provisions standard accounts (Stores Income, Student Fees, Salary
     * Expense) on first use so admins don't need to manually set up the
     * chart of accounts before the system can post entries.
     */
    private LedgerBooks resolveAccount(String institutionCode, String accountName,
                                       String accountCode, String accountType, String description) {
        return ledgerBooksRepository
                .findByInstitutionCodeAndAccountName(institutionCode, accountName)
                .orElseGet(() -> {
                    log.info("Auto-creating ledger account '{}' for {}", accountName, institutionCode);
                    return ledgerBooksRepository.save(LedgerBooks.builder()
                            .institutionCode(institutionCode)
                            .accountName(accountName)
                            .accountCode(accountCode)
                            .accountType(accountType)
                            .description(description)
                            .currentBalance(0.0)
                            .active(true)
                            .createdAt(LocalDateTime.now())
                            .build());
                });
    }

    /**
     * Posts a journal entry and updates the account's running balance.
     * <p>
     * For INCOME / LIABILITY / EQUITY accounts: CREDIT increases balance, DEBIT decreases.
     * For ASSET / EXPENSE accounts: DEBIT increases balance, CREDIT decreases.
     */
    private LedgerRecords postEntry(LedgerBooks book, String institutionCode,
                                    String entryType, Double amount, String sourceType,
                                    String sourceReference, String externalRef,
                                    String description, String postedBy, LocalDateTime entryDate) {

        boolean creditNormal = "INCOME".equals(book.getAccountType())
                || "LIABILITY".equals(book.getAccountType())
                || "EQUITY".equals(book.getAccountType());

        double delta;
        if (creditNormal) {
            delta = "CREDIT".equals(entryType) ? amount : -amount;
        } else {
            delta = "DEBIT".equals(entryType) ? amount : -amount;
        }

        double newBalance = (book.getCurrentBalance() != null ? book.getCurrentBalance() : 0.0) + delta;
        book.setCurrentBalance(newBalance);
        ledgerBooksRepository.save(book);

        LedgerRecords record = LedgerRecords.builder()
                .institutionCode(institutionCode)
                .ledgerBookId(book.getLedgerBookId())
                .entryType(entryType)
                .amount(amount)
                .description(description)
                .sourceType(sourceType)
                .sourceReference(sourceReference)
                .externalReference(externalRef)
                .postedBy(postedBy)
                .entryDate(entryDate)
                .postedAt(LocalDateTime.now())
                .build();

        return ledgerRecordsRepository.save(record);
    }

    private LedgerBooksResponse toBookResponse(LedgerBooks b) {
        return LedgerBooksResponse.builder()
                .ledgerBookId(b.getLedgerBookId())
                .institutionCode(b.getInstitutionCode())
                .accountName(b.getAccountName())
                .accountCode(b.getAccountCode())
                .accountType(b.getAccountType())
                .description(b.getDescription())
                .currentBalance(b.getCurrentBalance())
                .build();
    }

    private LedgerRecordsResponse toRecordResponse(LedgerRecords r) {
        return LedgerRecordsResponse.builder()
                .ledgerRecordId(r.getLedgerRecordId())
                .institutionCode(r.getInstitutionCode())
                .ledgerBookId(r.getLedgerBookId())
                .entryType(r.getEntryType())
                .amount(r.getAmount())
                .description(r.getDescription())
                .sourceType(r.getSourceType())
                .sourceReference(r.getSourceReference())
                .externalReference(r.getExternalReference())
                .postedBy(r.getPostedBy())
                .entryDate(r.getEntryDate())
                .postedAt(r.getPostedAt())
                .build();
    }
}

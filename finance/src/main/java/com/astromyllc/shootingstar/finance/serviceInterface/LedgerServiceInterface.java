package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.LedgerBooksRequest;
import com.astromyllc.shootingstar.finance.dto.request.LedgerFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.LedgerRecordsFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.StoreSaleLedgerRequest;
import com.astromyllc.shootingstar.finance.dto.response.LedgerBooksResponse;
import com.astromyllc.shootingstar.finance.dto.response.LedgerRecordsResponse;

import java.util.List;

public interface LedgerServiceInterface {

    /**
     * POST /api/finance/ledger/create — create or edit a chart-of-accounts entry
     */
    LedgerBooksResponse createOrUpdateAccount(LedgerBooksRequest request);

    /**
     * POST /api/finance/ledger/get-by-institution — chart of accounts, optional type filter
     */
    List<LedgerBooksResponse> getAccountsByInstitution(LedgerFetchRequest request);

    /**
     * POST /api/finance/ledger/get-records — journal entries for one account
     */
    List<LedgerRecordsResponse> getRecordsByAccount(LedgerRecordsFetchRequest request);

    /**
     * POST /api/finance/ledger/store-sale
     * Called by the Stores-Inventory service when a StoreOrder is fulfilled.
     * Auto-provisions the "Stores & Prospectus Income" INCOME account on
     * first use, then posts a CREDIT journal entry and updates currentBalance.
     * Idempotent on (orderRef, sourceType=STORE_SALE).
     */
    LedgerRecordsResponse postStoreSale(StoreSaleLedgerRequest request);

    /**
     * Called when a Bill_Payment is saved — posts a CREDIT to the
     * "Student Fees Income" INCOME account. Idempotent on (BP-<id>, STUDENT_PAYMENT).
     */
    LedgerRecordsResponse postStudentPayment(String institutionCode, Long billPaymentId,
                                             String studentId, Double amount,
                                             String paymentMethod, String externalRef,
                                             String description, String postedBy);

    /**
     * Called when a salary run is marked PAID — posts a DEBIT to the
     * "Salary Expense" EXPENSE account. Idempotent on (SAL-<id>, SALARY).
     */
    LedgerRecordsResponse postSalaryExpense(String institutionCode, Long salaryId,
                                            String staffName, Double netSalary,
                                            String paymentMethod, String externalRef,
                                            String postedBy);
}

package com.astromyllc.shootingstar.library.repository;

import com.astromyllc.shootingstar.library.model.LibraryLoan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LibraryLoanRepository extends MongoRepository<LibraryLoan, String> {

    List<LibraryLoan> findByInstitutionCode(String institutionCode);

    List<LibraryLoan> findByInstitutionCodeAndStatus(String institutionCode, String status);

    List<LibraryLoan> findByInstitutionCodeAndStudentIndex(String institutionCode, String studentIndex);

    /** Overdue = still ACTIVE and past due date — computed here at query time, not a stored status. */
    List<LibraryLoan> findByInstitutionCodeAndStatusAndDueDateBefore(
            String institutionCode, String status, LocalDateTime asOf);

    /**
     * Most recent ACTIVE loan for a given book+student — used to resolve a return
     * when the caller (e.g. Pulse) only knows bookCode + studentIndex, not the loanId.
     */
    Optional<LibraryLoan> findFirstByInstitutionCodeAndBookCodeAndStudentIndexAndStatusOrderByCheckoutDateDesc(
            String institutionCode, String bookCode, String studentIndex, String status);

    /** Prevents the same student from having two simultaneous active loans on the same book. */
    Optional<LibraryLoan> findByInstitutionCodeAndBookCodeAndStudentIndexAndStatus(
            String institutionCode, String bookCode, String studentIndex, String status);
}

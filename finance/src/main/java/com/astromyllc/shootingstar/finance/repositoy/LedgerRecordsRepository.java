package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.LedgerRecords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LedgerRecordsRepository extends JpaRepository<LedgerRecords, Long> {

    List<LedgerRecords> findByInstitutionCodeAndLedgerBookIdOrderByEntryDateDesc(
            String institutionCode, Long ledgerBookId);

    List<LedgerRecords> findByInstitutionCodeAndLedgerBookIdAndEntryDateBetweenOrderByEntryDateDesc(
            String institutionCode, Long ledgerBookId, LocalDateTime from, LocalDateTime to);

    List<LedgerRecords> findByInstitutionCodeAndSourceType(String institutionCode, String sourceType);

    /** Idempotency check: has this source event already been posted? */
    Optional<LedgerRecords> findBySourceReferenceAndSourceType(String sourceReference, String sourceType);
}

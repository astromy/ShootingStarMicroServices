package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.LedgerBooks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LedgerBooksRepository extends JpaRepository<LedgerBooks, Long> {

    Optional<LedgerBooks> findByInstitutionCodeAndAccountName(String institutionCode, String accountName);

    List<LedgerBooks> findByInstitutionCodeAndActiveTrue(String institutionCode);

    List<LedgerBooks> findByInstitutionCodeAndAccountTypeAndActiveTrue(String institutionCode, String accountType);
}

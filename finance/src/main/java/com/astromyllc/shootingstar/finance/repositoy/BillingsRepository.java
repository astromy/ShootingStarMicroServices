package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.Billings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingsRepository extends JpaRepository<Billings,Long> {
}

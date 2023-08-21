package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill,Long> {
}
